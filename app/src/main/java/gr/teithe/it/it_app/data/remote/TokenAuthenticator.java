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

package gr.teithe.it.it_app.data.remote;

import androidx.annotation.Nullable;

import gr.teithe.it.it_app.data.local.preference.PreferencesManager;
import gr.teithe.it.it_app.data.model.TokenResponse;
import gr.teithe.it.it_app.data.repository.TokenRepository;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator
{
    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @Nullable Response response) throws IOException
    {
        if(response != null && response.code() == 401)
        {
            TokenResponse token = new TokenRepository().refreshToken().execute().body();

            if(token != null && token.getRefreshToken() != null)
            {
                PreferencesManager.setRefreshToken(token.getRefreshToken());
                PreferencesManager.setAccessToken(token.getAccessToken());

                return response.request().newBuilder()
                        .header("x-access-token", token.getAccessToken())
                        .build();
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
