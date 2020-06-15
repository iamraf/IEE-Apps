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

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import gr.teithe.it.it_app.data.model.Announcement;

public class AnnouncementsDataSourceFactory extends DataSource.Factory<Long, Announcement>
{
    private MutableLiveData<AnnouncementsDataSource> announcementsDataSource = new MutableLiveData<>();

    @Override
    public DataSource<Long, Announcement> create()
    {
        AnnouncementsDataSource dataSource = new AnnouncementsDataSource();

        announcementsDataSource.postValue(dataSource);

        return dataSource;
    }

    public MutableLiveData<AnnouncementsDataSource> getAnnouncementsDataSource()
    {
        return announcementsDataSource;
    }
}
