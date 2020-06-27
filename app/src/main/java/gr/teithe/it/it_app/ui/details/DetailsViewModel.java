/*
 * Copyright (C) 2018-2020 Raf
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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.data.model.File;
import gr.teithe.it.it_app.data.repository.AnnouncementsRepository;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DetailsViewModel extends ViewModel
{
    private AnnouncementsRepository announcementsRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<Announcement> announcement;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;

    private MutableLiveData<File> file;

    public DetailsViewModel()
    {
        announcementsRepository = new AnnouncementsRepository();
        disposable = new CompositeDisposable();

        announcement = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();

        file = new MutableLiveData<>();
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

                    if(announce.getAttachments() != null)
                    {
                        loadFiles(announce.getAttachments());
                    }
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

    private void loadFiles(ArrayList<String> attachments)
    {
        for(String fileId : attachments)
        {
            disposable.add(announcementsRepository.getFile(fileId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(filer -> file.postValue(filer), throwable -> {}));
        }
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

    public LiveData<File> getFile()
    {
        return file;
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();

        disposable.dispose();
    }
}
