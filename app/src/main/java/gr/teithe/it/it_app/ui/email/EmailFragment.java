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

package gr.teithe.it.it_app.ui.email;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
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
import gr.teithe.it.it_app.databinding.FragmentEmailBinding;

public class EmailFragment extends Fragment
{
    private FragmentEmailBinding mDataBinding;
    private EmailViewModel mViewModel;

    private AlertDialog mProgressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_email, container, false);

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

        mViewModel = new ViewModelProvider(this).get(EmailViewModel.class);

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
                Toast.makeText(getContext(), "Η αλλαγή email έγινε επιτυχώς", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(mDataBinding.getRoot()).popBackStack();
            }
            else
            {
                Toast.makeText(getContext(), "Η αλλαγή email απέτυχε", Toast.LENGTH_SHORT).show();
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
        if(item.getItemId() == R.id.m_check)
        {
            if(mDataBinding.fEmailText.getText().toString().isEmpty())
            {
                Toast.makeText(getContext(), "Το πεδία δεν μπορουν να είναι κενά", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mViewModel.changeEmail(mDataBinding.fEmailText.getText().toString());
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
