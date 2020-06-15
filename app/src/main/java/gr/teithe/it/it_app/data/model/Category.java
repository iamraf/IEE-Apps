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

package gr.teithe.it.it_app.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Category
{
    @SerializedName("_id")
    @NonNull
    private String id;
    @NonNull
    private String name;
    @Nullable
    private ArrayList<String> registered;

    public Category(@NonNull String id, @NonNull String name, @Nullable ArrayList<String> registered)
    {
        this.id = id;
        this.name = name;
        this.registered = registered;
    }

    @NonNull
    public String getId()
    {
        return id;
    }

    public void setName(@NonNull String name)
    {
        this.name = name;
    }

    @NonNull
    public String getName()
    {
        return name;
    }

    public boolean isRegistered()
    {
        return registered != null && registered.size() > 0 && registered.get(0).equals("true");
    }
}
