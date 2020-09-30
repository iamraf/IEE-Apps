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

package gr.teithe.it.it_app.data.repository;

import gr.teithe.it.it_app.data.model.Category;
import gr.teithe.it.it_app.data.remote.ApiClient;
import gr.teithe.it.it_app.data.remote.ApiService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CategoriesRepository
{
    private ApiService apiService;

    public CategoriesRepository()
    {
        apiService = ApiClient.getClient();
    }

    public Call<List<Category>> getSyncCategories(String path)
    {
        return apiService.getSyncCategories(path);
    }

    public Observable<List<Category>> getCategories(String path)
    {
        return apiService.getCategories(path);
    }

    public Observable<List<Category>> getRegisteredCategories()
    {
        return apiService.getRegisteredCategories();
    }

    public Observable<Response<ResponseBody>> putCategories(RequestBody body)
    {
        return apiService.putCategories(body);
    }
}
