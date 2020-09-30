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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.local.preference.PreferencesManager;
import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.databinding.FragmentAnnouncementsBinding;

import gr.teithe.it.it_app.ui.main.MainActivity;
import gr.teithe.it.it_app.util.BadgeDrawable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class AnnouncementsFragment extends Fragment implements AnnouncementsAdapter.AnnouncementsAdapterListener
{
    private FragmentAnnouncementsBinding mDataBinding;
    private AnnouncementsViewModel mViewModel;

    private AnnouncementsAdapter mAdapter;

    private Snackbar mSnackBar;

    private String mNotificationCount = "0";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_announcements, container, false);

        mAdapter = new AnnouncementsAdapter(this);

        mDataBinding.fAnnouncementsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataBinding.fAnnouncementsRecycler.setHasFixedSize(true);
        mDataBinding.fAnnouncementsRecycler.setAdapter(mAdapter);

        mDataBinding.fAnnouncementsSwipe.setOnRefreshListener(() -> mViewModel.refreshData());

        mSnackBar = Snackbar.make(mDataBinding.fAnnouncementsCoordinator, "Συνδεθείτε για την πλήρη λειτουργία της εφαρμογής", Snackbar.LENGTH_SHORT);
        mSnackBar.setAction("Συνδεση", view ->
        {
            NavController navController = Navigation.findNavController(mDataBinding.getRoot());

            if(navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.announcementsFragment)
            {
                navController.navigate(AnnouncementsFragmentDirections.announcementsToAuthentication());
            }
        });
        mSnackBar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE);

        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        //TODO: Method too long, maybe break down

        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(AnnouncementsViewModel.class);

        mViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                mSnackBar.dismiss();
                mDataBinding.fAnnouncementsMessage.setVisibility(View.GONE);

                if(mAdapter.getItemCount() == 0)
                {
                    mDataBinding.fAnnouncementsProgress.setVisibility(View.VISIBLE);
                    mDataBinding.fAnnouncementsSwipe.setRefreshing(false);

                }
                else
                {
                    mDataBinding.fAnnouncementsProgress.setVisibility(View.GONE);
                    mDataBinding.fAnnouncementsSwipe.setRefreshing(true);
                }
            }
            else
            {
                mDataBinding.fAnnouncementsProgress.setVisibility(View.GONE);
                mDataBinding.fAnnouncementsSwipe.setRefreshing(false);
            }
        });

        mViewModel.isLoggedIn().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(!aBoolean)
            {
                if(!PreferencesManager.getRefreshToken().isEmpty())
                {
                    PreferencesManager.setRefreshToken("");

                    startActivity(new Intent(getActivity(), MainActivity.class));
                }

                mSnackBar.show();
            }
            else
            {
                mSnackBar.dismiss();

                mViewModel.loadNotificationCount();
            }
        });

        mViewModel.getAnnouncements().observe(getViewLifecycleOwner(), announcements ->
        {
            if(announcements.size() != 0)
            {
                mAdapter.submitList(announcements);

                mDataBinding.fAnnouncementsMessage.setVisibility(View.GONE);
                mDataBinding.fAnnouncementsRecycler.setVisibility(View.VISIBLE);
            }
            else
            {
                if(mDataBinding.fAnnouncementsMessage.getVisibility() != View.VISIBLE)
                {
                    mDataBinding.fAnnouncementsMessage.setText("Δεν βρέθηκαν ανακοινώσεις");

                    mDataBinding.fAnnouncementsMessage.setVisibility(View.VISIBLE);
                    mDataBinding.fAnnouncementsRecycler.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s ->
        {
            if(mAdapter.getItemCount() == 0)
            {
                mDataBinding.fAnnouncementsMessage.setText(s);

                mDataBinding.fAnnouncementsMessage.setVisibility(View.VISIBLE);
                mDataBinding.fAnnouncementsRecycler.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getNotificationCount().observe(getViewLifecycleOwner(), s ->
        {
            mNotificationCount = s;

            Activity activity = getActivity();

            if(activity != null)
            {
                activity.invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.announcements_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.m_announcements_notifications);

        if(PreferencesManager.getRefreshToken().isEmpty())
        {
            menuItem.setVisible(false);
        }
        else
        {
            LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

            setBadgeCount(getContext(), icon, mNotificationCount);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.m_announcements_search)
        {
            NavController navController = Navigation.findNavController(mDataBinding.getRoot());

            if(navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.announcementsFragment)
            {
                navController.navigate(AnnouncementsFragmentDirections.announcementsToSearch());
            }

            return true;
        }
        else if(item.getItemId() == R.id.m_announcements_notifications)
        {
            NavController navController = Navigation.findNavController(mDataBinding.getRoot());

            if(navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.announcementsFragment)
            {
                navController.navigate(AnnouncementsFragmentDirections.announcementsToNotifications());
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClicked(Announcement announcement)
    {
        AnnouncementsFragmentDirections.AnounencementsToDetails action = AnnouncementsFragmentDirections.anounencementsToDetails(announcement.getId());
        action.setTitle(announcement.getCategory());
        action.setId(announcement.getId());

        mViewModel.sendNotification(announcement);

        NavController navController = Navigation.findNavController(mDataBinding.getRoot());

        if(navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.announcementsFragment)
        {
            navController.navigate(action);
        }
    }

    private void setBadgeCount(Context context, LayerDrawable icon, String count)
    {
        if(context != null)
        {
            BadgeDrawable badge;
            Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);

            if(reuse instanceof BadgeDrawable)
            {
                badge = (BadgeDrawable) reuse;
            }
            else
            {
                badge = new BadgeDrawable(context);
            }

            badge.setCount(count);

            icon.mutate();
            icon.setDrawableByLayerId(R.id.ic_badge, badge);
        }
    }
}
