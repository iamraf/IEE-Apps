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

package gr.teithe.it.it_app.util;

public class Constants
{
    public static final String CLIENT_ID = "YOUR_ID";
    public static final String CLIENT_SECRET = "YOUR_SECRET";

    public static final String RESPONSE_URL = "https://github.com/h01d/IEE-Apps";
    public static final String POLICY_URL = "https://github.com/h01d/IEE-Apps/blob/master/PRIVACY-POLICY.md";

    public static final String API_URL = "https://api.iee.ihu.gr/";
    public static final String LOGIN_URL = "https://login.iee.ihu.gr/";
    public static final String TOKEN_URL = "https://login.iee.ihu.gr/token";
    public static final String AUTHORIZATION_URL = "https://login.iee.ihu.gr/authorization/?client_id=" + CLIENT_ID + "&redirect_uri=" + RESPONSE_URL + "&response_type=code&scope=announcements,profile,notifications,refresh_token,edit_mail,edit_password,edit_profile,edit_notifications";
    public static final String COPY_URL = "https://apps.iee.ihu.gr/announcements/announcement/";

    public static final String GRAND_TYPE_AUTHORIZATION = "authorization_code";
    public static final String GRAND_TYPE_REFRESH_TOKEN = "refresh_token";
}
