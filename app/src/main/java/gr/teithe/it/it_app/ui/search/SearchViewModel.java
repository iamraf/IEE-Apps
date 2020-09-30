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

package gr.teithe.it.it_app.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.data.model.Category;
import gr.teithe.it.it_app.data.repository.AnnouncementsRepository;
import gr.teithe.it.it_app.data.repository.CategoriesRepository;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchViewModel extends ViewModel
{
    private AnnouncementsRepository announcementsRepository;
    private CategoriesRepository categoriesRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<List<Announcement>> announcements;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;

    private List<Category> categories;

    public SearchViewModel()
    {
        announcementsRepository = new AnnouncementsRepository();
        categoriesRepository = new CategoriesRepository();
        disposable = new CompositeDisposable();

        announcements = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();

        categories = new ArrayList<>();

        loadCategories("categories");
    }

    public void searchAnnouncement(String query)
    {
        loadAnnouncements(query, "announcements");
    }

    private void loadCategories(String path)
    {
        //TODO: Maybe find a better way to implement it

        disposable.add(categoriesRepository.getCategories(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryList ->
                {
                    categories.clear();
                    categories.addAll(categoryList);
                }, throwable ->
                {
                    if(throwable.getMessage() != null && throwable.getMessage().contains("401"))
                    {
                        loadCategories("categories/public");
                    }
                }));
    }

    private void loadAnnouncements(String query, String announcementsPath)
    {
        //TODO: Implement filtering through API when issue gets fixed (https://github.com/apavlidi/IT_API/issues/90)

        isLoading.setValue(true);

        if(!categories.isEmpty())
        {
            disposable.add(announcementsRepository.getAnnouncements(announcementsPath)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(announcementsList ->
                    {
                        List<Announcement> list = new ArrayList<>();

                        for(Announcement announcement : announcementsList)
                        {
                            for(Category category : categories)
                            {
                                if(announcement.getAbout().equals(category.getId()))
                                {
                                    announcement.setCategory(category.getName());

                                    if(announcement.getTitle().toLowerCase().contains(query.toLowerCase()) || announcement.getText().toLowerCase().contains(query.toLowerCase()) || announcement.getPublisher().getName().toLowerCase().contains(query.toLowerCase()) || announcement.getCategory().toLowerCase().contains(query.toLowerCase()))
                                    {
                                        list.add(announcement);
                                        break;
                                    }
                                }
                            }
                        }

                        if(list.isEmpty())
                        {
                            announcements.postValue(list);
                        }
                        else
                        {
                            errorMessage.postValue("Δεν βρέθηκαν αποτελέσματα");
                        }

                        announcements.postValue(list);

                        isLoading.setValue(false);
                    }, throwable ->
                    {
                        if(throwable.getMessage() != null && throwable.getMessage().contains("401"))
                        {
                            loadAnnouncements(query, "announcements/public");
                        }
                        else if(throwable instanceof UnknownHostException)
                        {
                            errorMessage.postValue("Δεν υπάρχει πρόσβαση στο διαδίκτυο");
                        }
                        else if(throwable instanceof SocketTimeoutException)
                        {
                            errorMessage.postValue("Η σύνδεση έλειξε, δοκιμάστε ξανά");
                        }
                        else
                        {
                            errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα\n" + throwable.getMessage());
                        }

                        isLoading.setValue(false);
                    }));
        }
        else
        {
            errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα.\nΑνοίξτε το παράθυρο αναζήτησης ξανά");
            isLoading.setValue(false);
        }
    }

    public LiveData<List<Announcement>> getAnnouncements()
    {
        return announcements;
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
