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

package gr.teithe.it.it_app.ui.announcements;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.data.model.Category;
import gr.teithe.it.it_app.data.repository.AnnouncementsRepository;
import gr.teithe.it.it_app.data.repository.CategoriesRepository;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class AnnouncementsDataSource extends PageKeyedDataSource<Long, Announcement>
{
    private AnnouncementsRepository announcementsRepository;
    private CategoriesRepository categoriesRepository;

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> isLoggedIn;
    private MutableLiveData<String> errorMessage;

    private String path;
    private List<Category> categories;

    public AnnouncementsDataSource()
    {
        announcementsRepository = new AnnouncementsRepository();
        categoriesRepository = new CategoriesRepository();

        isLoading = new MutableLiveData<>();
        isLoggedIn = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();

        categories = new ArrayList<>();
    }

    public MutableLiveData<Boolean> isLoading()
    {
        return isLoading;
    }

    public MutableLiveData<Boolean> isLoggedIn()
    {
        return isLoggedIn;
    }

    public MutableLiveData<String> getErrorMessage()
    {
        return errorMessage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Announcement> callback)
    {
        isLoading.postValue(true);

        try
        {
            Response<List<Category>> categoriesResponse = categoriesRepository.getSyncCategories("categories").execute();

            if(categoriesResponse.isSuccessful() && categoriesResponse.body() != null)
            {
                isLoggedIn.postValue(true);

                path = "announcements";
                categories.addAll(categoriesResponse.body());
            }
            else
            {
                if(categoriesResponse.code() == 401)
                {
                    isLoggedIn.postValue(false);

                    path = "announcements/public";

                    categoriesResponse = categoriesRepository.getSyncCategories("categories/public").execute();

                    if(categoriesResponse.isSuccessful() && categoriesResponse.body() != null)
                    {
                        categories.addAll(categoriesResponse.body());
                    }
                    else
                    {
                        errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα");
                        return;
                    }
                }
                else if(categoriesResponse.code() == 404 || categoriesResponse.code() == 502 || categoriesResponse.code() == 503)
                {
                    errorMessage.postValue("Η υπηρεσία δεν είναι διαθέσιμη αυτή την στιγμή");
                    return;
                }
                else
                {
                    errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα");
                    return;
                }
            }

            Response<List<Announcement>> response = announcementsRepository.getPagedAnnouncements(path, 15, 0L).execute();

            if(response.isSuccessful() && response.body() != null)
            {
                List<Announcement> announcements = response.body();

                for(Announcement tmp : announcements)
                {
                    for(Category category : categories)
                    {
                        if(tmp.getAbout().equals(category.getId()))
                        {
                            tmp.setCategory(category.getName());
                            break;
                        }
                    }
                }

                callback.onResult(announcements, null, 1L);
            }
            else
            {
                errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα");
            }
        }
        catch(UnknownHostException e)
        {
            errorMessage.postValue("Δεν υπάρχει πρόσβαση στο διαδίκτυο");
        }
        catch(SocketTimeoutException e)
        {
            errorMessage.postValue("Η σύνδεση έλειξε, δοκιμάστε ξανά");
        }
        catch(IOException e)
        {
            errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα\n" + e.getMessage());
        }

        isLoading.postValue(false);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Announcement> callback)
    {
        //Do nothing
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Announcement> callback)
    {
        try
        {
            Response<List<Announcement>> response = announcementsRepository.getPagedAnnouncements(path, params.requestedLoadSize, params.key).execute();

            if(response.isSuccessful() && response.body() != null)
            {
                List<Announcement> announcements = response.body();

                for(Announcement tmp : announcements)
                {
                    for(Category category : categories)
                    {
                        if(tmp.getAbout().equals(category.getId()))
                        {
                            tmp.setCategory(category.getName());
                            break;
                        }
                    }
                }

                callback.onResult(announcements, params.key + 1);
            }
            else
            {
                errorMessage.postValue("Αδυναμία φόρτωσης περισσότερων ανακοινώσεων");
            }
        }
        catch(IOException e)
        {
            errorMessage.postValue("Αδυναμία φόρτωσης περισσότερων ανακοινώσεων");
        }
    }
}
