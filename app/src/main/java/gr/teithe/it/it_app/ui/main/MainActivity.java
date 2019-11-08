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

package gr.teithe.it.it_app.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.local.preference.PreferencesManager;
import gr.teithe.it.it_app.databinding.ActivityMainBinding;
import gr.teithe.it.it_app.util.ThemeHelper;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferencesManager.init(this);

        applyTheme();

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mDataBinding.aMainToolbar.setTitle("Ανακοινώσεις");
        setSupportActionBar(mDataBinding.aMainToolbar);

        setupNavigation();
    }

    private void applyTheme()
    {
        ThemeHelper.applyTheme(PreferencesManager.getTheme());

        if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void setupNavigation()
    {
        NavController navController = Navigation.findNavController(this, R.id.a_main_host);
        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
        {
            switch(destination.getId())
            {
                case R.id.announcementsFragment:
                    if(PreferencesManager.getRefreshToken().isEmpty())
                    {
                        mDataBinding.aMainBottom.setVisibility(View.GONE);
                    }
                    else
                    {
                        mDataBinding.aMainBottom.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.profileFragment:
                case R.id.settingsFragment:
                    mDataBinding.aMainBottom.setVisibility(View.VISIBLE);
                    break;
                default:
                    mDataBinding.aMainBottom.setVisibility(View.GONE);
                    break;
            }

            View view = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if(view != null && imm != null)
            {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        NavigationUI.setupWithNavController(mDataBinding.aMainToolbar, navController, new AppBarConfiguration.Builder(R.id.announcementsFragment, R.id.profileFragment, R.id.settingsFragment).build());
        NavigationUI.setupWithNavController(mDataBinding.aMainBottom, navController);
    }
}
