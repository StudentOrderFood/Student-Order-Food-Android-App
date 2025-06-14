package prm392.orderfood.data.repositoryImpl.auth;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import prm392.orderfood.data.datasource.local.TokenLocalDataSource;
import prm392.orderfood.data.datasource.remote.AuthDataSource;
import prm392.orderfood.data.mapper.TokenMapper;
import prm392.orderfood.data.mapper.UserMapper;
import prm392.orderfood.domain.models.Token;
import prm392.orderfood.domain.models.User;
import prm392.orderfood.domain.repositories.auth.AuthRepository;
import retrofit2.Response;

public class AuthRepositoryImpl implements AuthRepository {
    private final AuthDataSource authDataSource;
    private final TokenLocalDataSource tokenLocalDataSource;

    @Inject
    public AuthRepositoryImpl(AuthDataSource authDataSource, TokenLocalDataSource tokenLocalDataSource) {
        this.authDataSource = authDataSource;
        this.tokenLocalDataSource = tokenLocalDataSource;
    }

    @Override
    public Single<Response<Token>> logIn() {
        return authDataSource.sendTokenToServer()
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    if (response.isSuccessful() && response.body() != null) {
                        Token token = TokenMapper.mapToDomain(response.body());
                        tokenLocalDataSource.saveAccessToken(token.getAccessToken());
                        return Response.success(token);
                    } else {
                        return Response.error(response.code(), response.errorBody());
                    }
                });
    }

    @Override
    public User getCurrentUser() {
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        // Return mapped domain User or null
//        if (firebaseUser == null) {
//            return null; // No user signed in
//        }
//        return UserMapper.mapToDomain(firebaseUser);
        return null;
    }

    @Override
    public Task<Response<Void>> logOut() {
        tokenLocalDataSource.clearToken();
        authDataSource.logOut();
        return Tasks.forResult(Response.success(null));
    }

    @Override
    public Task<String> getIdToken() {
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser != null) {
//            // Force refresh (true) to ensure we get a valid, non-expired token
//            return currentUser.getIdToken(true).continueWithTask(task -> {
//                if (task.isSuccessful()) {
//                    return Tasks.forResult(task.getResult().getToken());
//                } else {
//                    // Handle error getting token
//                    return Tasks.forException(Objects.requireNonNull(task.getException()));
//                }
//            });
//        } else {
//            // No user signed in
//            return Tasks.forResult(null);
//        }
        return null;
    }
}
