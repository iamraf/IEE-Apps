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

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import gr.teithe.it.it_app.R;

public class User
{
    @NonNull
    private String am;
    @SerializedName("cn;lang-el")
    @NonNull
    private String fullName;
    @SerializedName("description;lang-el")
    @NonNull
    private String description;
    @SerializedName("displayName;lang-el")
    @NonNull
    private String displayName;
    @SerializedName("eduPersonPrimaryAffiliation")
    @NonNull
    private String category;
    @SerializedName("labeledURI")
    @NonNull
    private String website;
    @NonNull
    private String mail;
    @SerializedName("pwdChangedTime")
    @NonNull
    private String lastPasswordChange;
    @SerializedName("secondarymail")
    @NonNull
    private String secondaryMail;
    @SerializedName("sem")
    @NonNull
    private String currentSemester;
    @NonNull
    private String telephoneNumber;
    @SerializedName("title;lang-el")
    @NonNull
    private String title;
    @NonNull
    private String profilePhoto;
    @NonNull
    private SocialMedia socialMedia;

    public User(@NonNull String am, @NonNull String fullName, @NonNull String description, @NonNull String displayName, @NonNull String category, @NonNull String website, @NonNull String mail, @NonNull String lastPasswordChange, @NonNull String secondaryMail, @NonNull String currentSemester, @NonNull String telephoneNumber, @NonNull String title, @NonNull String profilePhoto, @NonNull SocialMedia socialMedia)
    {
        this.am = am;
        this.fullName = fullName;
        this.description = description;
        this.displayName = displayName;
        this.category = category;
        this.website = website;
        this.mail = mail;
        this.lastPasswordChange = lastPasswordChange;
        this.secondaryMail = secondaryMail;
        this.currentSemester = currentSemester;
        this.telephoneNumber = telephoneNumber;
        this.title = title;
        this.profilePhoto = profilePhoto;
        this.socialMedia = socialMedia;
    }

    @NonNull
    public String getAm()
    {
        return am;
    }

    @NonNull
    public String getFullName()
    {
        return fullName;
    }

    @NonNull
    public String getDescription()
    {
        return description;
    }

    @NonNull
    public String getDisplayName()
    {
        return displayName;
    }

    @NonNull
    public String getCategory()
    {
        return category;
    }

    @NonNull
    public String getWebsite()
    {
        return website;
    }

    @NonNull
    public String getMail()
    {
        return mail;
    }

    @NonNull
    public String getLastPasswordChange()
    {
        return lastPasswordChange;
    }

    @NonNull
    public String getSecondaryMail()
    {
        return secondaryMail;
    }

    @NonNull
    public String getCurrentSemester()
    {
        return currentSemester;
    }

    @NonNull
    public String getTelephoneNumber()
    {
        return telephoneNumber;
    }

    @NonNull
    public String getTitle()
    {
        return title;
    }

    @NonNull
    public String getProfilePhoto()
    {
        return profilePhoto;
    }

    @NonNull
    public SocialMedia getSocialMedia()
    {
        return socialMedia;
    }

    public static class SocialMedia
    {
        @Nullable
        private String facebook;
        @Nullable
        private String twitter;
        @Nullable
        private String github;
        @Nullable
        private String linkedIn;

        public SocialMedia(@Nullable String facebook, @Nullable String twitter, @Nullable String github, @Nullable String linkedIn)
        {
            this.facebook = facebook;
            this.twitter = twitter;
            this.github = github;
            this.linkedIn = linkedIn;
        }

        @Nullable
        public String getFacebook()
        {
            return facebook;
        }

        @Nullable
        public String getTwitter()
        {
            return twitter;
        }

        @Nullable
        public String getGithub()
        {
            return github;
        }

        @Nullable
        public String getLinkedIn()
        {
            return linkedIn;
        }
    }

    @BindingAdapter(value = "pwchange")
    public static void setPasswordChange(TextView textView, String date)
    {
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date tmp = simpleDateFormat.parse(date);

            textView.setText("Τελευταία αλλαγή κωδικού: " + new SimpleDateFormat("d MMMM", new Locale("el", "GR")).format(tmp));
        }
        catch(Exception e)
        {
            textView.setText("Τελευταία αλλαγή κωδικού:");
        }
    }

    @BindingAdapter("profilePhoto")
    public static void setProfilePhoto(ImageView view, String path)
    {
        if(path == null || path.isEmpty())
        {
            view.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
        else
        {
            Glide.with(view.getContext())
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .error(R.drawable.ic_account_circle_black_24dp)
                            .circleCrop())
                    .into(view);
        }
    }
}
