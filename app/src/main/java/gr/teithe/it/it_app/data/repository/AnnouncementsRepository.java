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

import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.data.model.File;
import gr.teithe.it.it_app.data.remote.ApiClient;
import gr.teithe.it.it_app.data.remote.ApiService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;

public class AnnouncementsRepository
{
    private ApiService apiService;

    public AnnouncementsRepository()
    {
        apiService = ApiClient.getClient();
    }

    public Call<List<Announcement>> getPagedAnnouncements(String path, int pageSize, long page)
    {
        return apiService.getPagingAnnouncements(path, pageSize, page);
    }

    public Observable<List<Announcement>> getAnnouncements(String path)
    {
        return apiService.getAnnouncements(path);
    }

    public Observable<Announcement> getAnnouncement(String id)
    {
        return apiService.getAnnouncement(id);
    }

    public Observable<File> getFile(String id)
    {
        return apiService.getFile(id);
    }
}
