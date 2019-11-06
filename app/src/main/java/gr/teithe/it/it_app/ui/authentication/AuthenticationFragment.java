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

package gr.teithe.it.it_app.ui.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import gr.teithe.it.it_app.R;
import gr.teithe.it.it_app.databinding.FragmentAuthenticationBinding;
import gr.teithe.it.it_app.ui.main.MainActivity;
import gr.teithe.it.it_app.util.Constants;

public class AuthenticationFragment extends Fragment
{
    private FragmentAuthenticationBinding mDataBinding;
    private AuthenticationViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_authentication, container, false);

        return mDataBinding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);

        mViewModel.isAuthenticated().observe(getViewLifecycleOwner(), aBoolean ->
        {
            if(aBoolean)
            {
                Toast.makeText(getContext(), "Η σύνδεση έγινε επιτυχώς", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getContext(), "Η σύνδεση απέτυχε", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(mDataBinding.getRoot()).popBackStack();
            }
        });

        mDataBinding.fAuthenticationWeb.getSettings().setJavaScriptEnabled(true);
        mDataBinding.fAuthenticationWeb.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wView, String url)
            {
                if(url.contains(Constants.LOGIN_URL))
                {
                    mDataBinding.fAuthenticationWeb.loadUrl(url);
                    return true;
                }
                else
                {
                    if(url.contains(Constants.RESPONSE_URL))
                    {
                        if(url.contains("error"))
                        {
                            Toast.makeText(getContext(), "Δεν δώθηκαν δικαιώματα στην εφαρμογή", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(mDataBinding.getRoot()).popBackStack();

                            return false;
                        }
                        else
                        {
                            Uri uri = Uri.parse(url);
                            final String code = uri.getQueryParameter("code");

                            mViewModel.authenticate(code);

                            return false;
                        }
                    }
                    else
                    {
                        mDataBinding.fAuthenticationWeb.loadUrl(Constants.AUTHORIZATION_URL);
                        return true;
                    }
                }
            }
        });

        mDataBinding.fAuthenticationWeb.loadUrl(Constants.AUTHORIZATION_URL);
    }
}
