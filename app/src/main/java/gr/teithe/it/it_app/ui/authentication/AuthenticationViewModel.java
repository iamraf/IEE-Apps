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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gr.teithe.it.it_app.data.repository.TokenRepository;
import gr.teithe.it.it_app.data.local.preference.PreferencesManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AuthenticationViewModel extends ViewModel
{
    private TokenRepository repository;
    private CompositeDisposable disposable;

    private MutableLiveData<Boolean> authenticated;

    public AuthenticationViewModel()
    {
        repository = new TokenRepository();
        disposable = new CompositeDisposable();

        authenticated = new MutableLiveData<>();
    }

    public void authenticate(String code)
    {
        disposable.add(repository.authenticate(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokenResponse ->
                {
                    if(tokenResponse != null && tokenResponse.getRefreshToken() != null)
                    {
                        PreferencesManager.setRefreshToken(tokenResponse.getRefreshToken());
                        PreferencesManager.setAccessToken(tokenResponse.getAccessToken());

                        authenticated.setValue(true);
                    }
                    else
                    {
                        authenticated.setValue(false);
                    }
                }, throwable -> authenticated.setValue(false)));
    }

    public LiveData<Boolean> isAuthenticated()
    {
        return authenticated;
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();

        disposable.dispose();
    }
}
