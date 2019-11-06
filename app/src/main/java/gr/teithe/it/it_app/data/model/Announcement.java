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

package gr.teithe.it.it_app.data.model;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Announcement implements Serializable
{
    @NonNull
    @SerializedName("_id")
    private String id;
    @NonNull
    @SerializedName("_about")
    private String about;
    @NonNull
    private String category;
    @NonNull
    private String title;
    @NonNull
    private String text;
    @NonNull
    private Publisher publisher;
    @NonNull
    private String date;
    @Nullable
    private ArrayList<String> attachments;

    public Announcement(@NonNull String id, @NonNull String about, @NonNull String title, @NonNull String text, @NonNull Publisher publisher, @NonNull String date, @Nullable ArrayList<String> attachments)
    {
        this.id = id;
        this.about = about;
        this.title = title;
        this.text = text;
        this.publisher = publisher;
        this.date = date;
        this.attachments = attachments;
    }

    @NonNull
    public String getId()
    {
        return id;
    }

    @NonNull
    public String getAbout()
    {
        return about;
    }

    @NonNull
    public String getCategory()
    {
        return category;
    }

    public void setCategory(@NonNull String category)
    {
        this.category = category;
    }

    @NonNull
    public String getTitle()
    {
        return title;
    }

    @NonNull
    public String getText()
    {
        return text;
    }

    @NonNull
    public Publisher getPublisher()
    {
        return publisher;
    }

    @NonNull
    public String getDate()
    {
        return date;
    }

    @Nullable
    public ArrayList<String> getAttachments()
    {
        return attachments;
    }

    public static class Publisher implements Serializable
    {
        @NonNull
        private String name;

        public Publisher(@NonNull String name)
        {
            this.name = name;
        }

        @NonNull
        public String getName()
        {
            return name;
        }
    }

    @BindingAdapter(value = "date")
    public static void setDate(TextView textView, String date)
    {
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date tmp = simpleDateFormat.parse(date);

            if(tmp != null)
            {
                textView.setText(new SimpleDateFormat("d MMMM yyyy HH:mm", new Locale("el", "GR")).format(tmp));
            }
            else
            {
                textView.setText("");
            }
        }
        catch(Exception e)
        {
            textView.setText("");
        }
    }
}
