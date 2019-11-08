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

package gr.teithe.it.it_app.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import gr.teithe.it.it_app.BuildConfig;
import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.local.preference.PreferencesManager;
import gr.teithe.it.it_app.ui.main.MainActivity;

import gr.teithe.it.it_app.util.Constants;
import gr.teithe.it.it_app.util.ThemeHelper;

public class SettingsFragment extends PreferenceFragmentCompat
{
    private SettingsViewModel mViewModel;

    private AlertDialog mProgressDialog;

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        setPreferencesFromResource(R.xml.settings, s);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setMessage("Παρακαλώ περιμένετε...");

        mProgressDialog = builder.create();

        Preference logout = findPreference("pref_logout");
        if(logout != null)
        {
            logout.setOnPreferenceClickListener(preference ->
            {
                Activity activity = getActivity();

                if(activity != null)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Αποσύνδεση")
                            .setMessage("Είστε σίγουροι ότι θέλετε να αποσυνδεθείτε")
                            .setPositiveButton("Ναι", (dialog, which) ->
                            {
                                PreferencesManager.setRefreshToken("");
                                PreferencesManager.setAccessToken("");

                                PreferencesManager.setNotifications(false);

                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            })
                            .setNegativeButton("Οχι", null)
                            .show();

                    return true;
                }

                return false;
            });
        }

        ListPreference themePreference = findPreference("pref_theme");
        if(themePreference != null)
        {
            themePreference.setOnPreferenceChangeListener((preference, newValue) ->
            {
                ThemeHelper.applyTheme((String) newValue);

                return true;
            });
        }

        Preference email = findPreference("pref_change_email");
        if(email != null)
        {
            email.setOnPreferenceClickListener(preference ->
            {
                View view = getView();

                if(view != null)
                {
                    Navigation.findNavController(view).navigate(SettingsFragmentDirections.settingsToEmail());

                    return true;
                }
                else
                {
                    return false;
                }
            });
        }

        Preference password = findPreference("pref_change_password");
        if(password != null)
        {
            password.setOnPreferenceClickListener(preference ->
            {
                View view = getView();

                if(view != null)
                {
                    Navigation.findNavController(view).navigate(SettingsFragmentDirections.settingsToPassword());

                    return true;
                }
                else
                {
                    return false;
                }
            });
        }

        Preference categories = findPreference("pref_categories");
        if(categories != null)
        {
            categories.setOnPreferenceClickListener(preference ->
            {
                View view = getView();

                if(view != null)
                {
                    Navigation.findNavController(view).navigate(SettingsFragmentDirections.settingsToCategories());

                    return true;
                }
                else
                {
                    return false;
                }
            });
        }

        Preference privacy = findPreference("pref_privacy");
        if(privacy != null)
        {
            privacy.setOnPreferenceClickListener(preference ->
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POLICY_URL)));
                return true;
            });
        }

        Preference bug = findPreference("pref_bug");
        if(bug != null)
        {
            bug.setOnPreferenceClickListener(preference ->
            {
                String data = "mailto:rafaellos.g9@gmail.com" +
                        "?subject=" + Uri.encode("IEE Apps " + BuildConfig.VERSION_NAME + " - Αναφορά προβλήματος (" + Build.MODEL + ", " + Build.VERSION.SDK_INT + ")") +
                        "&body=" + Uri.encode("Αναφέρετε το πρόβλημα σας εδώ:\n");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(data));

                startActivity(emailIntent);

                return true;
            });
        }

        SwitchPreference notifications = findPreference("pref_notifications");
        if(notifications != null)
        {
            notifications.setOnPreferenceChangeListener((preference, newValue) ->
            {
                if(newValue instanceof Boolean)
                {
                    Boolean flag = (Boolean) newValue;

                    if(flag)
                    {
                        mViewModel.syncNotificationCategories();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Οι ειδοποιήσεις απενεργοποιήθηκαν", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            });
        }

        Preference about = findPreference("pref_about");
        if(about != null)
        {
            about.setSummary(BuildConfig.VERSION_NAME);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        mViewModel.isUpdating().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                mProgressDialog.show();
            }
            else
            {
                mProgressDialog.dismiss();
            }
        });

        mViewModel.isSuccess().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                Toast.makeText(getContext(), "Οι ειδοποιήσεις ενεργοποιήθηκαν επιτυχώς", Toast.LENGTH_SHORT).show();
            }
            else
            {
                PreferencesManager.setNotifications(false);
                Toast.makeText(getContext(), "Οι ειδοποιήσεις δεν ενεργοποιήθηκαν", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
