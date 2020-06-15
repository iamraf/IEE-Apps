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

package gr.teithe.it.it_app.data.local.preference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferencesManager
{
    private static SharedPreferences instance;

    public static void init(Context context)
    {
        if(instance == null)
        {
            instance = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static String getRefreshToken()
    {
        return instance.getString("refresh_token", "");
    }

    public static void setRefreshToken(String refreshToken)
    {
        SharedPreferences.Editor prefsEditor = instance.edit();
        prefsEditor.putString("refresh_token", refreshToken);
        prefsEditor.apply();
    }

    public static String getAccessToken()
    {
        return instance.getString("access_token", "");
    }

    public static void setAccessToken(String accessToken)
    {
        SharedPreferences.Editor prefsEditor = instance.edit();
        prefsEditor.putString("access_token", accessToken);
        prefsEditor.apply();
    }

    public static String getTheme()
    {
        return instance.getString("pref_theme", "default");
    }

    public static boolean isReceivingNotifications()
    {
        return instance.getBoolean("pref_notifications", false);
    }

    public static void setNotifications(boolean flag)
    {
        SharedPreferences.Editor prefsEditor = instance.edit();
        prefsEditor.putBoolean("pref_notifications", flag);
        prefsEditor.apply();
    }
}
