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

package gr.teithe.it.it_app.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.model.Notification;
import gr.teithe.it.it_app.data.repository.NotificationsRepository;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationsViewModel extends ViewModel
{
    private NotificationsRepository notificationsRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<List<Notification>> notificationsList;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;

    public NotificationsViewModel()
    {
        notificationsRepository = new NotificationsRepository();
        disposable = new CompositeDisposable();

        notificationsList = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();

        loadData();
    }

    private void loadData()
    {
        isLoading.setValue(true);

        disposable.add(notificationsRepository.getNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsResponse ->
                {
                    boolean update = false;

                    List<Notification> list = new ArrayList<>();

                    for(Notification tmp : notificationsResponse.getNotifications())
                    {
                        if(tmp.getNotification().getRelated().getId() != null)
                        {
                            list.add(tmp);
                        }

                        if(!tmp.isSeen())
                        {
                            update = true;
                        }
                    }
                    if(list.size() == 0)
                    {
                        errorMessage.setValue("No items");
                    }
                    else
                    {
                        Collections.reverse(list);

                        notificationsList.setValue(list);
                    }

                    if(update)
                    {
                        updateNotifications();
                    }

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

    public void updateNotifications()
    {
        disposable.add(notificationsRepository.postNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public LiveData<List<Notification>> getNotifications()
    {
        return notificationsList;
    }

    public LiveData<String> getErrorMessage()
    {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading()
    {
        return isLoading;
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();

        disposable.dispose();
    }
}
