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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.data.model.Announcement;
import gr.teithe.it.it_app.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment implements SearchAdapter.SearchAdapterListener
{
    private FragmentSearchBinding mDataBinding;
    private SearchViewModel mViewModel;

    private SearchAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        mAdapter = new SearchAdapter(this);

        mDataBinding.fSearchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataBinding.fSearchRecycler.setHasFixedSize(true);
        mDataBinding.fSearchRecycler.setAdapter(mAdapter);

        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        mViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                mDataBinding.fSearchMessage.setVisibility(View.GONE);
                mDataBinding.fSearchProgress.setVisibility(View.VISIBLE);
                mDataBinding.fSearchRecycler.setVisibility(View.GONE);
            }
            else
            {
                mDataBinding.fSearchProgress.setVisibility(View.GONE);
            }
        });

        mViewModel.getAnnouncements().observe(getViewLifecycleOwner(), announcements ->
        {
            if(announcements.size() != 0)
            {
                mAdapter.setData(announcements);

                mDataBinding.fSearchMessage.setVisibility(View.GONE);
                mDataBinding.fSearchRecycler.setVisibility(View.VISIBLE);
            }
            else
            {
                mDataBinding.fSearchMessage.setText("Δεν βρέθηκαν ανακοινώσεις");

                mDataBinding.fSearchMessage.setVisibility(View.VISIBLE);
                mDataBinding.fSearchRecycler.setVisibility(View.GONE);
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s ->
        {
            mDataBinding.fSearchMessage.setText(s);

            mDataBinding.fSearchMessage.setVisibility(View.VISIBLE);
            mDataBinding.fSearchRecycler.setVisibility(View.GONE);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.m_search);
        SearchView mSearchView = (SearchView) item.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setIconified(false);
        mSearchView.setOnCloseListener(() -> true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if(!query.isEmpty())
                {
                    mViewModel.searchAnnouncement(query);

                    Activity activity = getActivity();

                    if(activity != null)
                    {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

                        if(imm != null)
                        {
                            imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if(newText.isEmpty() && mDataBinding.fSearchProgress.getVisibility() == View.GONE)
                {
                    mDataBinding.fSearchMessage.setVisibility(View.GONE);
                    mDataBinding.fSearchRecycler.setVisibility(View.GONE);
                }

                return false;
            }
        });
    }

    @Override
    public void onClicked(Announcement announcement)
    {
        SearchFragmentDirections.SearchToDetails action = SearchFragmentDirections.searchToDetails(announcement.getId());
        action.setTitle(announcement.getCategory());
        action.setId(announcement.getId());

        Navigation.findNavController(mDataBinding.getRoot()).navigate(action);
    }
}
