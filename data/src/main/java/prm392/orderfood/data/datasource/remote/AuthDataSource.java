package prm392.orderfood.data.datasource.remote;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.data.datasource.remote.api.AuthApiService;
import prm392.orderfood.data.datasource.remote.modelRequest.IdTokenRequest;
import prm392.orderfood.data.datasource.remote.modelResponse.TokenResponse;
import retrofit2.Response;

public class AuthDataSource {
    private final FirebaseAuth firebaseAuth;
    private final AuthApiService apiService;

    @Inject
    public AuthDataSource(FirebaseAuth firebaseAuth, AuthApiService apiService) {
        this.firebaseAuth = firebaseAuth;
        this.apiService = apiService;
    }

    public Single<Response<TokenResponse>> sendTokenToServer() {
        return getFirebaseIdToken()
                .flatMap(idToken -> apiService.sendIdToken(new IdTokenRequest(idToken)));
    }

    public Completable logOut() {
        return Completable.create(emitter -> {
            try {
                firebaseAuth.signOut();
                emitter.onComplete(); // Thành công
            } catch (Exception e) {
                emitter.onError(e); // Có lỗi khi signOut
            }
        });
    }

    private Single<String> getFirebaseIdToken() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            return Single.error(new Throwable("User not logged in"));
        }

        if (user.getEmail() == null || !user.getEmail().endsWith(".edu.vn")) {
            return Single.error(new Throwable("Only .edu.vn emails allowed"));
        }

        return Single.fromCallable(() -> {
            // Chạy Blocking để lấy Token một cách đồng bộ
            return Tasks.await(user.getIdToken(true), 10, TimeUnit.SECONDS).getToken();
        }).subscribeOn(Schedulers.io());
    }
}
