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

package gr.teithe.it.it_app.ui.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gr.teithe.it.it_app.databinding.AnnouncementItemBinding;

import gr.teithe.it.it_app.data.model.Announcement;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>
{
    private List<Announcement> data;

    private SearchAdapterListener listener;

    public SearchAdapter(SearchAdapterListener listener)
    {
        this.listener = listener;

        data = new ArrayList<>();
    }

    public void setData(List<Announcement> announcements)
    {
        data.clear();
        data.addAll(announcements);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        AnnouncementItemBinding binding = AnnouncementItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position)
    {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder
    {
        private final AnnouncementItemBinding binding;

        public SearchViewHolder(@NonNull AnnouncementItemBinding binding)
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

    public interface SearchAdapterListener
    {
        void onClicked(Announcement announcement);
    }
}
