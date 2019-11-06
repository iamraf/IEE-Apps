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

package gr.teithe.it.it_app.ui.email;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.repository.ProfileRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EmailViewModel extends ViewModel
{
    private ProfileRepository profileRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<Boolean> isSuccess;
    private MutableLiveData<Boolean> isUpdating;

    public EmailViewModel()
    {
        profileRepository = new ProfileRepository();
        disposable = new CompositeDisposable();

        isSuccess = new MutableLiveData<>();
        isUpdating = new MutableLiveData<>();
    }

    public void changeEmail(String newMail)
    {
        isUpdating.postValue(true);

        disposable.add(profileRepository.postChangeEmail(newMail)
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
