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

import gr.teithe.it.it_app.data.local.preference.PreferencesManager;
import gr.teithe.it.it_app.data.model.TokenResponse;
import gr.teithe.it.it_app.data.remote.ApiClient;
import gr.teithe.it.it_app.data.remote.ApiService;
import gr.teithe.it.it_app.util.Constants;

import io.reactivex.Observable;
import retrofit2.Call;

public class TokenRepository
{
    private ApiService apiService;

    public TokenRepository()
    {
        apiService = ApiClient.getClient();
    }

    public Observable<TokenResponse> authenticate(String code)
    {
        return apiService.authenticate(Constants.TOKEN_URL, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.GRAND_TYPE_AUTHORIZATION, code);
    }

    public Call<TokenResponse> refreshToken()
    {
        return apiService.refreshToken(Constants.TOKEN_URL, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.GRAND_TYPE_REFRESH_TOKEN, PreferencesManager.getRefreshToken());
    }
}
