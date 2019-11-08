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

package gr.teithe.it.it_app.ui.announcements;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.data.model.Notification;
import gr.teithe.it.it_app.data.repository.NotificationsRepository;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementsViewModel extends ViewModel
{
    private NotificationsRepository notificationsRepository;
    private CompositeDisposable disposable;

    private LiveData<AnnouncementsDataSource> liveDataSource;

    private LiveData<PagedList<Announcement>> announcementsPagedList;

    private LiveData<Boolean> isLoading;
    private LiveData<Boolean> isLoggedIn;
    private LiveData<String> errorMessage;

    private MutableLiveData<String> notificationCount;

    public AnnouncementsViewModel()
    {
        notificationsRepository = new NotificationsRepository();
        disposable = new CompositeDisposable();

        AnnouncementsDataSourceFactory dataSourceFactory = new AnnouncementsDataSourceFactory();

        liveDataSource = dataSourceFactory.getAnnouncementsDataSource();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(15)
                .build();

        announcementsPagedList = new LivePagedListBuilder<>(dataSourceFactory, config).build();

        isLoading = Transformations.switchMap(liveDataSource, AnnouncementsDataSource::isLoading);
        isLoggedIn = Transformations.switchMap(liveDataSource, AnnouncementsDataSource::isLoggedIn);
        errorMessage = Transformations.switchMap(liveDataSource, AnnouncementsDataSource::getErrorMessage);

        notificationCount = new MutableLiveData<>();
    }

    public void refreshData()
    {
        if(liveDataSource.getValue() != null)
        {
            liveDataSource.getValue().invalidate();
        }
    }

    public void loadNotificationCount()
    {
        disposable.add(notificationsRepository.getNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsResponse ->
                {
                    int count = 0;

                    for(Notification notification : notificationsResponse.getNotifications())
                    {
                        if(!notification.isSeen())
                        {
                            count++;
                        }
                    }

                    notificationCount.postValue(String.valueOf(count));

                }, throwable ->
                {
                    //Do nothing
                }));
    }

    public void sendNotification(Announcement announcement)
    {
        //TODO: Maybe one day this gets added to API to make our life easier

        /*
            Notifications are implemented using Firebase Topic Messaging (https://firebase.google.com/docs/cloud-messaging/android/topic-messaging)
            I don't have access to API to send to topic when announcement gets added so each time the first device opens the new announcement, it
            inserts notification into a real-time database and triggers the send to topic function. (Using a script on Firebase Functions)
            Notification will be send once, later on every other request about the same notification will be ignored.
         */

        Map<String, String> map = new HashMap<>();
        map.put("about", announcement.getAbout());
        map.put("category", announcement.getCategory());
        map.put("title", announcement.getTitle());
        map.put("name", announcement.getPublisher().getName());
        map.put("date", announcement.getDate());

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(announcement.getId()).setValue(map);
    }

    public LiveData<PagedList<Announcement>> getAnnouncements()
    {
        return announcementsPagedList;
    }

    public LiveData<String> getErrorMessage()
    {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading()
    {
        return isLoading;
    }

    public LiveData<Boolean> isLoggedIn()
    {
        return isLoggedIn;
    }

    public LiveData<String> getNotificationCount()
    {
        return notificationCount;
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();

        disposable.dispose();
    }
}
