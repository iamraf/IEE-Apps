/*
 * Copyright (C) 2019 Raf
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gr.teithe.it.it_app.ui.details;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.data.repository.AnnouncementsRepository;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DetailsViewModel extends ViewModel
{
    private AnnouncementsRepository announcementsRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<Announcement> announcement;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;

    private MutableLiveData<Boolean> isSuccess;
    private MutableLiveData<Boolean> isDownloading;

    public DetailsViewModel()
    {
        announcementsRepository = new AnnouncementsRepository();
        disposable = new CompositeDisposable();

        announcement = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();

        isSuccess = new MutableLiveData<>();
        isDownloading = new MutableLiveData<>();
    }

    public void loadAnnouncement(String id)
    {
        isLoading.setValue(true);

        disposable.add(announcementsRepository.getAnnouncement(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announce ->
                {
                    announcement.setValue(announce);
                    isLoading.setValue(false);
                }, throwable ->
                {
                    if(throwable instanceof UnknownHostException)
                    {
                        errorMessage.postValue("Δεν υπάρχει πρόσβαση στο διαδίκτυο");
                    }
                    else if(throwable instanceof SocketTimeoutException)
                    {
                        errorMessage.postValue("Η σύνδεση έλειξε, δοκιμάστε ξανά");
                    }
                    else
                    {
                        errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα.\n" + throwable.getMessage());
                    }

                    isLoading.setValue(false);
                }));
    }

    public void downloadFiles(String id, Context context)
    {
        isDownloading.postValue(true);
        disposable.add(announcementsRepository.downloadFiles(id)
                .flatMap(response -> saveFile(response, id, context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(flag ->
                {
                    if(flag)
                    {
                        isSuccess.postValue(true);
                    }
                    else
                    {
                        isSuccess.postValue(false);
                    }

                    isDownloading.postValue(false);

                }, throwable ->
                {
                    isDownloading.postValue(false);
                    isSuccess.postValue(false);
                }));
    }

    private Observable<Boolean> saveFile(Response<ResponseBody> response, String id, Context context)
    {
        return Observable.create(emitter ->
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                final String relativeLocation = Environment.DIRECTORY_DOWNLOADS + File.separator + "IEE-Apps";

                final ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, relativeLocation);
                contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "f" + id);
                contentValues.put(MediaStore.Files.FileColumns.TITLE, "f" + id);
                contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/zip");

                final ContentResolver resolver = context.getContentResolver();

                OutputStream stream;
                Uri uri = null;

                try
                {
                    if(response.body() == null)
                    {
                        throw new IOException();
                    }

                    uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);

                    if(uri == null)
                    {
                        throw new IOException();
                    }

                    stream = resolver.openOutputStream(uri);

                    if(stream == null)
                    {
                        throw new IOException();
                    }

                    byte[] fileReader = new byte[8 * 1024];

                    while(true)
                    {
                        int read = response.body().byteStream().read(fileReader);

                        if(read == -1)
                        {
                            break;
                        }

                        stream.write(fileReader, 0, read);
                    }

                    stream.flush();
                    stream.close();

                    emitter.onNext(true);
                    emitter.onComplete();
                }
                catch(IOException e)
                {
                    if(uri != null)
                    {
                        resolver.delete(uri, null, null);
                    }

                    emitter.onError(e);
                }
            }
            else
            {
                try
                {
                    if(response.body() == null)
                    {
                        throw new IOException();
                    }

                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "f" + id + ".zip");

                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    sink.writeAll(response.body().source());
                    sink.close();

                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

                    if(downloadManager != null)
                    {
                        downloadManager.addCompletedDownload(file.getName(), file.getName(), true, "application/zip", file.getAbsolutePath(), file.length(), true);
                    }

                    emitter.onNext(true);
                    emitter.onComplete();
                }
                catch(IOException e)
                {
                    emitter.onError(e);
                }
            }
        });
    }

    public LiveData<Announcement> getAnnouncement()
    {
        return announcement;
    }

    public LiveData<String> getErrorMessage()
    {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading()
    {
        return isLoading;
    }

    public LiveData<Boolean> isSuccess()
    {
        return isSuccess;
    }

    public LiveData<Boolean> isDownloading()
    {
        return isDownloading;
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();

        disposable.dispose();
    }
}
