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

package gr.teithe.it.it_app.ui.categories;

import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.Toast;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.model.Category;

import gr.teithe.it.it_app.databinding.FragmentCategoriesBinding;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment
{
    private FragmentCategoriesBinding mDataBinding;
    private CategoriesViewModel mViewModel;

    private AlertDialog mProgressDialog;

    private ArrayList<String> mAddingList = new ArrayList<>();
    private ArrayList<String> mRemovingList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);

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

        mViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        mViewModel.getCategories().observe(getViewLifecycleOwner(), categories ->
        {
            for(Category category : categories)
            {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                checkBox.setText(category.getName());

                if(category.isRegistered())
                {
                    checkBox.setChecked(true);
                }

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                {
                    if(isChecked)
                    {
                        mRemovingList.remove(category.getId());

                        if(!category.isRegistered())
                        {
                            mAddingList.add(category.getId());
                        }
                    }
                    else
                    {
                        mAddingList.remove(category.getId());

                        if(category.isRegistered())
                        {
                            mRemovingList.add(category.getId());
                        }
                    }
                });

                mDataBinding.fCategoriesLinear.addView(checkBox);
            }

            mDataBinding.fCategoriesScroll.setVisibility(View.VISIBLE);
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s ->
        {
            mDataBinding.fCategoriesMessage.setText(s);
            mDataBinding.fCategoriesMessage.setVisibility(View.VISIBLE);
        });

        mViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                mDataBinding.fCategoriesProgress.setVisibility(View.VISIBLE);
                mDataBinding.fCategoriesScroll.setVisibility(View.GONE);
                mDataBinding.fCategoriesMessage.setVisibility(View.GONE);
            }
            else
            {
                mDataBinding.fCategoriesProgress.setVisibility(View.GONE);
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
                Navigation.findNavController(mDataBinding.getRoot()).popBackStack();
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
        if(item.getItemId() == R.id.m_check)
        {
            if(mAddingList.isEmpty() && mRemovingList.isEmpty())
            {
                Toast.makeText(getContext(), "Δεν έγιναν αλλαγές", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mViewModel.updateCategories(mAddingList, mRemovingList);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
