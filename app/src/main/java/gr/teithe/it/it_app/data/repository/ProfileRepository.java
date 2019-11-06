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

package gr.teithe.it.it_app.data.repository;

import gr.teithe.it.it_app.data.model.User;
import gr.teithe.it.it_app.data.remote.ApiClient;
import gr.teithe.it.it_app.data.remote.ApiService;

import io.reactivex.Observable;
import okhttp3.FormBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ProfileRepository
{
    private ApiService apiService;

    public ProfileRepository()
    {
        apiService = ApiClient.getClient();
    }

    public Observable<User> getProfile()
    {
        return apiService.getProfile();
    }

    public Observable<Response<ResponseBody>> postChangeEmail(String newMail)
    {
        return apiService.postChangeEmail(newMail);
    }

    public Observable<Response<ResponseBody>> postChangePassword(String oldPassword, String newPassword)
    {
        return apiService.postChangePassword(oldPassword, newPassword);
    }

    public Observable<Response<ResponseBody>> postProfile(FormBody body)
    {
        return apiService.postProfile(body);
    }
}
