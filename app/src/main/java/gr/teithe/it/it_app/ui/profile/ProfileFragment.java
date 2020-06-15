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

package gr.teithe.it.it_app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.model.User;
import gr.teithe.it.it_app.databinding.FragmentProfileBinding;

import okhttp3.FormBody;

public class ProfileFragment extends Fragment
{
    private FragmentProfileBinding mDataBinding;
    private ProfileViewModel mViewModel;

    private AlertDialog mProgressDialog;

    private User mUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setMessage("Παρακαλώ περιμένετε...");

        mProgressDialog = builder.create();

        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        mViewModel.getUser().observe(getViewLifecycleOwner(), user ->
        {
            mUser = user;

            mDataBinding.setUser(user);
            mDataBinding.fProfileScroll.setVisibility(View.VISIBLE);
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s ->
        {
            mDataBinding.fProfileMessage.setText(s);
            mDataBinding.fProfileMessage.setVisibility(View.VISIBLE);
        });

        mViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                mDataBinding.fProfileProgress.setVisibility(View.VISIBLE);
                mDataBinding.fProfileScroll.setVisibility(View.GONE);
                mDataBinding.fProfileMessage.setVisibility(View.GONE);
            }
            else
            {
                mDataBinding.fProfileProgress.setVisibility(View.GONE);
            }
        });

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
                Toast.makeText(getContext(), "Η αλλαγές έγιναν επιτυχώς", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Η αλλαγές δεν πραγματοποιήθηκαν", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.check_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.m_check && mDataBinding.getUser() != null)
        {
            //TODO: Find a way to check for changes inside ViewModel

            FormBody.Builder builder = new FormBody.Builder();

            if(!(mDataBinding.fProfileName.getText().toString() + " " + mDataBinding.fProfileLastname.getText().toString()).equals(mUser.getDisplayName()))
            {
                builder.add("displayName;lang-el", mDataBinding.fProfileName.getText().toString() + " " + mDataBinding.fProfileLastname.getText().toString());
            }

            if(!mDataBinding.fProfileMail.getText().toString().equals(mUser.getSecondaryMail()))
            {
                builder.add("secondarymail", mDataBinding.fProfileMail.getText().toString());
            }

            if(!mDataBinding.fProfilePhone.getText().toString().equals(mUser.getTelephoneNumber()))
            {
                builder.add("telephoneNumber", mDataBinding.fProfilePhone.getText().toString());
            }

            if(!mDataBinding.fProfileDescription.getText().toString().equals(mUser.getDescription()))
            {
                builder.add("description;lang-el", mDataBinding.fProfileDescription.getText().toString());
            }

            if(!mDataBinding.fProfileWebsite.getText().toString().equals(mUser.getWebsite()))
            {
                builder.add("labeledURI", mDataBinding.fProfileWebsite.getText().toString());
            }

            if(mUser.getSocialMedia() != null)
            {
                if(mUser.getSocialMedia().getFacebook() != null && !mDataBinding.fProfileFacebook.getText().toString().equals(mUser.getSocialMedia().getFacebook()))
                {
                    builder.add("facebook", mDataBinding.fProfileFacebook.getText().toString());
                }

                if(mUser.getSocialMedia().getTwitter() != null && !mDataBinding.fProfileTwitter.getText().toString().equals(mUser.getSocialMedia().getTwitter()))
                {
                    builder.add("twitter", mDataBinding.fProfileTwitter.getText().toString());
                }

                if(mUser.getSocialMedia().getGithub() != null && !mDataBinding.fProfileGithub.getText().toString().equals(mUser.getSocialMedia().getGithub()))
                {
                    builder.add("github", mDataBinding.fProfileGithub.getText().toString());
                }

                if(mUser.getSocialMedia().getLinkedIn() != null && !mDataBinding.fProfileLinkedin.getText().toString().equals(mUser.getSocialMedia().getLinkedIn()))
                {
                    builder.add("linkedIn", mDataBinding.fProfileLinkedin.getText().toString());
                }
            }

            FormBody body = builder.build();

            if(body.size() == 0)
            {
                Toast.makeText(getContext(), "Δεν έγιναν αλλαγές", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mViewModel.updateProfile(body);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
