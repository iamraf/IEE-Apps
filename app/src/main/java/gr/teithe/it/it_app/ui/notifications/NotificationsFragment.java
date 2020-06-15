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

package gr.teithe.it.it_app.ui.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.model.Notification;
import gr.teithe.it.it_app.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment implements NotificationsAdapter.NotificationsAdapterListener
{
    private FragmentNotificationsBinding mDataBinding;

    private NotificationsAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);

        mAdapter = new NotificationsAdapter(this);

        mDataBinding.fNotificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataBinding.fNotificationsRecycler.setHasFixedSize(true);
        mDataBinding.fNotificationsRecycler.setAdapter(mAdapter);

        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        NotificationsViewModel viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications ->
        {
            mAdapter.setData(notifications);

            mDataBinding.fNotificationsRecycler.setVisibility(View.VISIBLE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), s ->
        {
            mDataBinding.fNotificationsMessage.setText(s);
            mDataBinding.fNotificationsMessage.setVisibility(View.VISIBLE);
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                mDataBinding.fNotificationsProgress.setVisibility(View.VISIBLE);
                mDataBinding.fNotificationsRecycler.setVisibility(View.GONE);
                mDataBinding.fNotificationsMessage.setVisibility(View.GONE);
            }
            else
            {
                mDataBinding.fNotificationsProgress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClicked(Notification notification)
    {
        NotificationsFragmentDirections.NotificationsToDetails action = NotificationsFragmentDirections.notificationsToDetails(notification.getNotification().getRelated().getId().getId());
        action.setTitle(notification.getNotification().getRelated().getId().getAbout().getName());
        action.setId(notification.getNotification().getRelated().getId().getId());

        Navigation.findNavController(mDataBinding.getRoot()).navigate(action);
    }
}
