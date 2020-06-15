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

package gr.teithe.it.it_app.ui.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gr.teithe.it.it_app.data.model.File;
import gr.teithe.it.it_app.databinding.FileItemBinding;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder>
{
    private List<File> data;

    private FilesAdapter.FilesAdapterListener listener;

    public FilesAdapter(FilesAdapter.FilesAdapterListener listener)
    {
        this.listener = listener;

        data = new ArrayList<>();
    }

    public void addFile(File file)
    {
        data.add(file);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        FileItemBinding binding = FileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new FileViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position)
    {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder
    {
        private final FileItemBinding binding;

        public FileViewHolder(@NonNull FileItemBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(File item)
        {
            binding.getRoot().setOnClickListener(v -> listener.onClicked(binding.getFile()));
            binding.setFile(item);
            binding.executePendingBindings();
        }
    }

    public interface FilesAdapterListener
    {
        void onClicked(File file);
    }
}
