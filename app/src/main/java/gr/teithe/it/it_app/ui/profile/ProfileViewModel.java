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

package gr.teithe.it.it_app.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.model.User;
import gr.teithe.it.it_app.data.repository.ProfileRepository;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import okhttp3.FormBody;

public class ProfileViewModel extends ViewModel
{
    private ProfileRepository profileRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<User> userProfile;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;

    private MutableLiveData<Boolean> isSuccess;
    private MutableLiveData<Boolean> isUpdating;

    public ProfileViewModel()
    {
        profileRepository = new ProfileRepository();
        disposable = new CompositeDisposable();

        userProfile = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();

        isSuccess = new MutableLiveData<>();
        isUpdating = new MutableLiveData<>();

        loadData();
    }

    private void loadData()
    {
        isLoading.setValue(true);

        disposable.add(profileRepository.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user ->
                {
                    userProfile.setValue(user);
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

    public void updateProfile(FormBody body)
    {
        isUpdating.postValue(true);

        disposable.add(profileRepository.postProfile(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response ->
                {
                    isUpdating.postValue(false);

                    if(response.isSuccessful())
                    {
                        isSuccess.postValue(true);
                    }
                    else
                    {
                        isSuccess.postValue(false);
                    }
                }, throwable ->
                {
                    isUpdating.postValue(false);
                    isSuccess.postValue(false);
                }));
    }

    public LiveData<User> getUser()
    {
        return userProfile;
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

    public LiveData<Boolean> isUpdating()
    {
        return isUpdating;
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();

        disposable.dispose();
    }
}
