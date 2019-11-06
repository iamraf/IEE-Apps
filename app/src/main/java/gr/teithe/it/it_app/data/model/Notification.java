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

import android.graphics.Typeface;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Notification
{
    @SerializedName("_id")
    @NonNull
    private String id;
    @NonNull
    private boolean seen;

    @SerializedName("_notification")
    @NonNull
    private NotificationData notification;

    public Notification(@NonNull String id, @NonNull boolean seen, @NonNull NotificationData notification)
    {
        this.id = id;
        this.seen = seen;
        this.notification = notification;
    }

    @NonNull
    public String getId()
    {
        return id;
    }

    @NonNull
    public boolean isSeen()
    {
        return seen;
    }

    @NonNull
    public NotificationData getNotification()
    {
        return notification;
    }

    public static class NotificationData
    {
        @NonNull
        private String date;
        @SerializedName("nameEl")
        @NonNull
        private String name;
        @SerializedName("related")
        @NonNull
        private Related related;

        public NotificationData(@NonNull String date, @NonNull String name, @NonNull Related related)
        {
            this.date = date;
            this.name = name;
            this.related = related;
        }

        @NonNull
        public String getDate()
        {
            return date;
        }

        @NonNull
        public String getName()
        {
            return name;
        }

        @NonNull
        public Related getRelated()
        {
            return related;
        }
    }

    public static class Related
    {
        @Nullable
        @SerializedName("id")
        private Id id;

        public Related(@Nullable Id id)
        {
            this.id = id;
        }

        @Nullable
        public Id getId()
        {
            return id;
        }
    }

    public static class Id
    {
        @NonNull
        @SerializedName("_id")
        private String id;
        @SerializedName("_about")
        @NonNull
        private About about;
        @NonNull
        private String title;

        public Id(@NonNull String id, @NonNull About about, @NonNull String title)
        {
            this.id = id;
            this.about = about;
            this.title = title;
        }

        @NonNull
        public String getId()
        {
            return id;
        }

        @NonNull
        public About getAbout()
        {
            return about;
        }

        @NonNull
        public String getTitle()
        {
            return title;
        }
    }

    public static class About
    {
        @NonNull
        private String name;

        public About(@NonNull String name)
        {
            this.name = name;
        }

        @NonNull
        public String getName()
        {
            return name;
        }
    }

    @BindingAdapter(value = "notificationDate")
    public static void setDate(TextView textView, String date)
    {
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date tmp = simpleDateFormat.parse(date);

            textView.setText(new SimpleDateFormat("d MMMM yyyy HH:mm", new Locale("el", "GR")).format(tmp));
        }
        catch(Exception e)
        {
            textView.setText("");
        }
    }

    @BindingAdapter("isSeen")
    public static void setBold(TextView textView, boolean isSeen)
    {
        if(!isSeen)
        {
            textView.setTypeface(null, Typeface.ITALIC);
        }
    }
}
