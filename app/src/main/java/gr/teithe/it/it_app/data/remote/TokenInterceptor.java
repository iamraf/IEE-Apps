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

import androidx.annotation.NonNull;

import gr.teithe.it.it_app.data.local.preference.PreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor
{
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException
    {
        if(chain.request().url().toString().contains("login") || chain.request().url().toString().contains("public"))
        {
            return chain.proceed(chain.request());
        }
        else
        {
            return chain.proceed(chain.request().newBuilder()
                    .header("x-access-token", PreferencesManager.getAccessToken())
                    .build());
        }
    }
}
