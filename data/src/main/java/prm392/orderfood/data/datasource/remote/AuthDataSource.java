package prm392.orderfood.data.datasource.remote;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.data.datasource.remote.api.AuthApiService;
import prm392.orderfood.data.datasource.remote.modelRequest.auth.EmailRegisterRequest;
import prm392.orderfood.data.datasource.remote.modelRequest.auth.IdTokenRequest;
import prm392.orderfood.data.datasource.remote.modelRequest.auth.LoginRequest;
import prm392.orderfood.data.datasource.remote.modelRequest.auth.RegisterRequest;
import prm392.orderfood.data.datasource.remote.modelResponse.ApiResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.auth.TokenResponse;

public class AuthDataSource {
    private final FirebaseAuth firebaseAuth;
    private final AuthApiService apiService;

    @Inject
    public AuthDataSource(FirebaseAuth firebaseAuth, AuthApiService apiService) {
        this.firebaseAuth = firebaseAuth;
        this.apiService = apiService;
    }

    public Completable logOut() {
        return Completable.fromAction(firebaseAuth::signOut).subscribeOn(Schedulers.io());
    }

    public Single<ApiResponse<String>> registerShopOwner(RegisterRequest request) {
        return apiService.registerShopOwner(request)
                .subscribeOn(Schedulers.io());
    }

    public Single<ApiResponse<TokenResponse>> shopOwnerLogin(LoginRequest request) {
        return apiService.shopOwnerLogin(request)
                .subscribeOn(Schedulers.io());
    }

    public Single<ApiResponse<TokenResponse>> sendRawGoogleIdToken(String idToken) {
        Log.d("TOKEN_RAW", "Google ID Token: " + idToken);
        return apiService.sendIdToken(new IdTokenRequest(idToken));
    }

    public Single<ApiResponse<Void>> registerStudent(String idToken, String phoneNumber) {
        return  apiService.registerStudent(new EmailRegisterRequest(idToken, phoneNumber));
    }
}
