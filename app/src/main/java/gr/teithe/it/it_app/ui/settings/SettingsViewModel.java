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

package gr.teithe.it.it_app.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.local.preference.PreferencesManager;
import gr.teithe.it.it_app.data.model.Category;
import gr.teithe.it.it_app.data.repository.CategoriesRepository;
import com.google.firebase.messaging.FirebaseMessaging;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsViewModel extends ViewModel
{
    private CategoriesRepository categoriesRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<Boolean> isSuccess;
    private MutableLiveData<Boolean> isUpdating;

    public SettingsViewModel()
    {
        categoriesRepository = new CategoriesRepository();
        disposable = new CompositeDisposable();

        isSuccess = new MutableLiveData<>();
        isUpdating = new MutableLiveData<>();
    }

    public void syncNotificationCategories()
    {
        if(!PreferencesManager.getRefreshToken().isEmpty())
        {
            isUpdating.postValue(true);

            disposable.add(categoriesRepository.getRegisteredCategories()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(registeredCategories ->
                    {
                        for(Category category : registeredCategories)
                        {
                            if(category.isRegistered())
                            {
                                FirebaseMessaging.getInstance().subscribeToTopic(category.getId());
                            }
                            else
                            {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(category.getId());
                            }
                        }

                        isUpdating.postValue(false);
                        isSuccess.postValue(true);

                    }, throwable ->
                    {
                        isUpdating.postValue(false);
                        isSuccess.postValue(false);
                    }));
        }
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
