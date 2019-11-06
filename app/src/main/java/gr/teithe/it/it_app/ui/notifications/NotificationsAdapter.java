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

package gr.teithe.it.it_app.ui.notifications;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gr.teithe.it.it_app.data.model.Notification;
import gr.teithe.it.it_app.databinding.NotificationItemBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>
{
    private List<Notification> data;

    private NotificationsAdapter.NotificationsAdapterListener listener;

    public NotificationsAdapter(NotificationsAdapter.NotificationsAdapterListener listener)
    {
        this.listener = listener;

        data = new ArrayList<>();
    }

    public void setData(List<Notification> notifications)
    {
        data.clear();
        data.addAll(notifications);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        NotificationItemBinding binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position)
    {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        private final NotificationItemBinding binding;

        public NotificationViewHolder(@NonNull NotificationItemBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Notification item)
        {
            binding.getRoot().setOnClickListener(v -> listener.onClicked(binding.getNotification()));
            binding.setNotification(item);
            binding.executePendingBindings();
        }
    }

    public interface NotificationsAdapterListener
    {
        void onClicked(Notification notification);
    }
}
