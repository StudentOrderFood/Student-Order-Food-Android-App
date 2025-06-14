package prm392.orderfood.androidapp.ui.fragment.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.androidapp.ui.states.SignInState;
import prm392.orderfood.domain.models.User;
import prm392.orderfood.domain.usecase.AuthUseCase;
import retrofit2.Response;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final AuthUseCase mAuthUseCase;

    private final CompositeDisposable mCompositeDisposable;

    private final MutableLiveData<SignInState> mSignInState = new MutableLiveData<>(new SignInState.Idle());

    public LiveData<SignInState> getSignInState() {
        return mSignInState;
    }

    @Inject
    public AuthViewModel(AuthUseCase authUseCase) {
        this.mAuthUseCase = authUseCase;
        mCompositeDisposable = new CompositeDisposable();

    }

    public void loginWithGoogle() {
        mSignInState.setValue(new SignInState.Loading());

        Disposable disposable = mAuthUseCase.login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccessful() && response.body() != null) {
                                mSignInState.setValue(new SignInState.Success(response.body()));
                            } else if (response.errorBody() != null) {
                                mSignInState.setValue(new SignInState.Error("Error: " + response.code()));
                            } else {
                                mSignInState.setValue(new SignInState.Error("Unknown error"));
                            }
                        },
                        throwable -> {
                            String message = throwable.getMessage();
                            if (message == null || message.isEmpty()) {
                                message = throwable.toString(); // fallback để log ra tên class của exception
                            }
                            mSignInState.setValue(new SignInState.Error(message));
                        }                );
        mCompositeDisposable.add(disposable);
    }

    // Helper method to update state on Google Sign-In UI failure
    public void handleSignInError(String errorMessage) {
        mSignInState.setValue(new SignInState.Error(errorMessage));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear(); // Giải phóng bộ nhớ khi ViewModel bị hủy
    }
}
