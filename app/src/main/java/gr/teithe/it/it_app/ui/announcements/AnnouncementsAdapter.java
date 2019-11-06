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

package gr.teithe.it.it_app.ui.announcements;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.databinding.AnnouncementItemBinding;

public class AnnouncementsAdapter extends PagedListAdapter<Announcement, AnnouncementsAdapter.AnnouncementsViewHolder>
{
    private AnnouncementsAdapterListener listener;

    public AnnouncementsAdapter(AnnouncementsAdapterListener listener)
    {
        super(COMPARATOR);

        this.listener = listener;
    }

    @NonNull
    @Override
    public AnnouncementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        AnnouncementItemBinding binding = AnnouncementItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AnnouncementsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementsViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }

    public class AnnouncementsViewHolder extends RecyclerView.ViewHolder
    {
        private final AnnouncementItemBinding binding;

        public AnnouncementsViewHolder(@NonNull AnnouncementItemBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Announcement item)
        {
            binding.getRoot().setOnClickListener(v -> listener.onClicked(binding.getAnnouncement()));
            binding.setAnnouncement(item);
            binding.executePendingBindings();
        }
    }

    public interface AnnouncementsAdapterListener
    {
        void onClicked(Announcement announcement);
    }

    private static final DiffUtil.ItemCallback<Announcement> COMPARATOR = new DiffUtil.ItemCallback<Announcement>()
    {
        @Override
        public boolean areItemsTheSame(@NonNull Announcement oldItem, @NonNull Announcement newItem)
        {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Announcement oldItem, @NonNull Announcement newItem)
        {
            return oldItem.getId().equals(newItem.getId());
        }
    };
}