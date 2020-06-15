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

package gr.teithe.it.it_app.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.model.Category;
import gr.teithe.it.it_app.data.repository.CategoriesRepository;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CategoriesViewModel extends ViewModel
{
    private CategoriesRepository categoriesRepository;
    private CompositeDisposable disposable;

    private MutableLiveData<List<Category>> categories;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;

    private MutableLiveData<Boolean> isSuccess;
    private MutableLiveData<Boolean> isUpdating;

    public CategoriesViewModel()
    {
        categoriesRepository = new CategoriesRepository();
        disposable = new CompositeDisposable();

        categories = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();

        isSuccess = new MutableLiveData<>();
        isUpdating = new MutableLiveData<>();

        loadCategories();
    }

    private void loadCategories()
    {
        isLoading.setValue(true);

        disposable.add(categoriesRepository.getCategories("categories")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryList ->
                {
                    disposable.add(categoriesRepository.getRegisteredCategories()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(registeredCategories ->
                            {
                                for(Category category : registeredCategories)
                                {
                                    for(Category tmp : categoryList)
                                    {
                                        if(category.getId().equals(tmp.getId()))
                                        {
                                            category.setName(tmp.getName());
                                            break;
                                        }
                                    }
                                }

                                categories.postValue(registeredCategories);
                                isLoading.postValue(false);
                            }, throwable ->
                            {
                                errorMessage.postValue("Παρουσιάστηκε άγνωστο σφάλμα" + throwable.getMessage());
                            }));
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

    public void updateCategories(ArrayList<String> adding, ArrayList<String> removing)
    {
        isUpdating.postValue(true);

        try
        {
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonAdd = new JSONArray(adding);
            JSONArray jsonRemove = new JSONArray(removing);

            jsonObject.put("addCat", jsonAdd.toString());
            jsonObject.put("removeCat", jsonRemove.toString());

            disposable.add(categoriesRepository.putCategories(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response ->
                    {
                        if(response.isSuccessful())
                        {
                            for(String tmp : adding)
                            {
                                FirebaseMessaging.getInstance().subscribeToTopic(tmp);
                            }

                            for(String tmp : removing)
                            {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(tmp);
                            }

                            isSuccess.postValue(true);
                            isUpdating.postValue(false);
                        }
                        else
                        {
                            isSuccess.postValue(false);
                            isUpdating.postValue(false);
                        }
                    }, throwable ->
                    {
                        isUpdating.postValue(false);
                        isSuccess.postValue(false);
                    }));
        }
        catch(JSONException e)
        {
            isUpdating.postValue(false);
            isSuccess.postValue(false);
        }
    }

    public LiveData<List<Category>> getCategories()
    {
        return categories;
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
