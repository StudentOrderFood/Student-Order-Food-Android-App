package prm392.orderfood.androidapp.viewModel;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.androidapp.ui.states.SignInState;
import prm392.orderfood.androidapp.ui.states.SignUpState;
import prm392.orderfood.androidapp.utils.PhoneNumberUtils;
import prm392.orderfood.domain.models.users.UserRegister;
import prm392.orderfood.domain.usecase.AuthUseCase;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final AuthUseCase mAuthUseCase;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private final CompositeDisposable mCompositeDisposable;

    private final MutableLiveData<SignInState> mSignInState = new MutableLiveData<>(new SignInState.Idle());
    private final MutableLiveData<String> navigateTo = new MutableLiveData<>();
    private final MutableLiveData<SignUpState> mSignUpState = new MutableLiveData<>(new SignUpState.Idle());

    // Flag for OTP verification
    public final MutableLiveData<Boolean> otpSent = new MutableLiveData<>();
    // Flag for OTP verification result
    public final MutableLiveData<Boolean> otpVerified = new MutableLiveData<>();
    // LiveData for error messages
    public final MutableLiveData<String> error = new MutableLiveData<>();
    // LiveData for save phone number input
    public final MutableLiveData<String> phoneNumber = new MutableLiveData<>();


    public LiveData<SignInState> getSignInState() {
        return mSignInState;
    }

    public LiveData<String> getNavigateTo() {
        return navigateTo;
    }

    public LiveData<SignUpState> getSignUpState() {
        return mSignUpState;
    }

    @Inject
    public AuthViewModel(AuthUseCase authUseCase) {
        this.mAuthUseCase = authUseCase;
        mCompositeDisposable = new CompositeDisposable();
    }

    public void loginWithGoogle(String email, String idToken) {
        mSignInState.setValue(new SignInState.Loading());

        Disposable disposable = mAuthUseCase.isEmailRegistered(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isRegistered -> {
                    if (isRegistered) {
                        Disposable loginDisposable = mAuthUseCase.loginWithGoogle(idToken)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
                                    if (response.isSuccessful() && response.body() != null) {
                                        mSignInState.setValue(new SignInState.Success(response.body()));
                                    } else {
                                        try {
                                            String errorMsg = response.errorBody() != null
                                                    ? response.errorBody().string()
                                                    : "Unknown error";
                                            mSignInState.setValue(new SignInState.Error(errorMsg));
                                        } catch (IOException e) {
                                            mSignInState.setValue(new SignInState.Error("Error reading error body"));
                                        }
                                    }
                                }, throwable -> {
                                    String message = throwable.getMessage();
                                    if (message == null || message.isEmpty()) {
                                        message = throwable.toString();
                                    }
                                    mSignInState.setValue(new SignInState.Error(message));
                                });

                        mCompositeDisposable.add(loginDisposable);
                    } else {
                        // Email chưa đăng ký
                        navigateTo.setValue("phone_verification");
                    }
                }, throwable -> {
                    String message = throwable.getMessage();
                    if (message == null || message.isEmpty()) {
                        message = throwable.toString();
                    }
                    mSignInState.setValue(new SignInState.Error(message));
                });

        mCompositeDisposable.add(disposable);
    }

    public void loginShopOwner(String identifier, String password) {
        mSignInState.setValue(new SignInState.Loading());

        Disposable disposable = mAuthUseCase.shopOwnerLogin(identifier, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful() && response.body() != null) {
                        mSignInState.setValue(new SignInState.Success(response.body()));
                    } else if (response.errorBody() != null) {
                        mSignInState.setValue(new SignInState.Error(response.errorBody().string()));
                    } else {
                        mSignInState.setValue(new SignInState.Error("Unknown error"));
                    }
                }, throwable -> {
                    String message = throwable.getMessage();
                    if (message == null || message.isEmpty()) {
                        message = throwable.toString(); // fallback để log ra tên class của exception
                    }
                    mSignInState.setValue(new SignInState.Error(message));
                });

        mCompositeDisposable.add(disposable);
    }

    public void checkAuth() {
        Disposable disposable = mAuthUseCase.validateAccessToken()
                .subscribeOn(Schedulers.io()) // Run on background thread
                .observeOn(AndroidSchedulers.mainThread()) // Observe on main thread for UI
                .subscribe(response -> {
                    if (Boolean.TRUE.equals(response.body())) {
                        navigateTo.setValue("home");
                    } else {
                        navigateTo.setValue("login");
                    }
                }, throwable -> {
                    navigateTo.setValue("login");
                });
        mCompositeDisposable.add(disposable);
    }

    public void logout(Runnable onSuccess) {
        Disposable disposable = mAuthUseCase.signOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    mSignInState.setValue(new SignInState.Idle());
                    onSuccess.run(); // gọi callback logout thành công
                }, throwable -> {
                    // Optional: xử lý lỗi nếu cần
                });

        mCompositeDisposable.add(disposable);
    }

    public void registerShopOwner(UserRegister register) {
        mSignUpState.setValue(new SignUpState.Loading());

        Disposable disposable = mAuthUseCase.registerShopOwner(register)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String successMsg = response.body();
                        mSignUpState.setValue(new SignUpState.Success(successMsg));
                    } else if (response.errorBody() != null) {
                        String errorMessage = response.errorBody().string();
                        mSignUpState.setValue(new SignUpState.Error(errorMessage));
                    } else {
                        mSignUpState.setValue(new SignUpState.Error("Unknown error"));
                    }
                }, throwable -> {
                    String message = throwable.getMessage();
                    if (message == null || message.isEmpty()) {
                        message = throwable.toString(); // fallback để log ra tên class của exception
                    }
                    mSignUpState.setValue(new SignUpState.Error(message));
                });

        mCompositeDisposable.add(disposable);
    }

    // Helper method to update state on Google Sign-In UI failure
    public void handleSignInError(String errorMessage) {
        mSignInState.setValue(new SignInState.Error(errorMessage));
    }

    /**
     * Gửi OTP đến số điện thoại người dùng
     */
    public void startPhoneVerification(String phone, Activity activity) {
        if (!PhoneNumberUtils.isValidPhoneNumber(phone)) {
            error.setValue("Invalid Phone Number");
            return;
        }
        phoneNumber.setValue(PhoneNumberUtils.formatVietnamesePhone(phone));

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(Objects.requireNonNull(phoneNumber.getValue())) // Số điện thoại đã được định dạng
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * Gửi lại OTP nếu cần
     */
    public void resendOtp(Activity activity) {
        String phone = phoneNumber.getValue();
        if (phone == null || phone.isEmpty()) {
            error.setValue("Invalid phone number");
            return;
        }

        startPhoneVerification(phone, activity);
    }

    /**
     * Xác minh mã OTP người dùng nhập
     */
    public void verifyOtpCode(String code) {
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            error.setValue("Verification ID is null");
        }
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    signInWithPhoneAuthCredential(credential); // auto verify
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    error.setValue("Verification failed: " + e.getMessage());
                }

                @Override
                public void onCodeSent(String verifId, PhoneAuthProvider.ForceResendingToken token) {
                    verificationId = verifId;
                    resendToken = token;
                    otpSent.setValue(true); // navigate to OTP screen
                }
            };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        otpVerified.setValue(true);
                    } else {
                        error.setValue("OTP verification failed");
                    }
                });
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear(); // Giải phóng bộ nhớ khi ViewModel bị hủy
    }
}
