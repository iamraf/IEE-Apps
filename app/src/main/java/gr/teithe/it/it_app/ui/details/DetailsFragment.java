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

package gr.teithe.it.it_app.ui.details;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.databinding.FragmentDetailsBinding;

import gr.teithe.it.it_app.util.Constants;

public class DetailsFragment extends Fragment
{
    private FragmentDetailsBinding mDataBinding;
    private DetailsViewModel mViewModel;

    private AlertDialog mProgressDialog;

    private String mAnnouncementId;
    private String mAnnouncementCategory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setMessage("Παρακαλώ περιμένετε...");

        mProgressDialog = builder.create();

        if(getArguments() != null)
        {
            mAnnouncementId = DetailsFragmentArgs.fromBundle(getArguments()).getId();
            mAnnouncementCategory = DetailsFragmentArgs.fromBundle(getArguments()).getTitle();
        }
        else
        {
            Navigation.findNavController(mDataBinding.getRoot()).popBackStack();
        }

        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        mViewModel.loadAnnouncement(mAnnouncementId);

        mViewModel.getAnnouncement().observe(getViewLifecycleOwner(), announcement ->
        {
            mDataBinding.setAnnouncement(announcement);
            mDataBinding.fDetailsScroll.setVisibility(View.VISIBLE);

            if(mAnnouncementCategory != null)
            {
                announcement.setCategory(mAnnouncementCategory);
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s ->
        {
            mDataBinding.fDetailsMessage.setText(s);
            mDataBinding.fDetailsMessage.setVisibility(View.VISIBLE);
        });

        mViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                mDataBinding.fDetailsProgress.setVisibility(View.VISIBLE);
                mDataBinding.fDetailsScroll.setVisibility(View.GONE);
                mDataBinding.fDetailsMessage.setVisibility(View.GONE);
            }
            else
            {
                mDataBinding.fDetailsProgress.setVisibility(View.GONE);
            }
        });

        mDataBinding.fDetailsFiles.setOnClickListener(v ->
        {
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 69 /*nice*/);
            }
            else
            {
                mViewModel.downloadFiles(mAnnouncementId, getContext());
            }
        });

        mViewModel.isDownloading().observe(getViewLifecycleOwner(), aBoolean ->
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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                    startActivity(intent);
                }
                else
                {
                    Context context = getContext();

                    if(context != null)
                    {
                        Toast.makeText(context, "Η λήψη έγινε επιτυχώς", Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(context)
                                .setTitle("Αρχεία")
                                .setMessage("Τα αρχεία κατεβαίνουν συμπιεσμένα σε μορφή zip. Για να μπορείτε να τα ανοίξετε πρέπει πρώτα να τα αποσυμπιέσετε κατεβάζοντας κάποιον αποσυμπιεστή.")
                                .setNegativeButton("ΚΛΕΙΣΙΜΟ", null)
                                .show();
                    }
                }
            }
            else
            {
                Context context = getContext();

                if(context != null)
                {
                    Toast.makeText(context, "Η λήψη αρχείου απέτυχε", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.m_details_copy)
        {
            Activity activity = getActivity();

            if(activity != null)
            {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                if(clipboard != null)
                {
                    ClipData clip = ClipData.newPlainText("announcement", Constants.COPY_URL + mAnnouncementId);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(activity, "Η αντιγραφή έγινε επιτυχώς", Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == 69 /*nice*/ && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            mViewModel.downloadFiles(mAnnouncementId, getContext());
        }
    }
}
