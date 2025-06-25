package prm392.orderfood.data.repositoryImpl;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import prm392.orderfood.data.datasource.local.TokenLocalDataSource;
import prm392.orderfood.data.datasource.remote.AuthDataSource;
import prm392.orderfood.data.mapper.TokenMapper;
import prm392.orderfood.domain.models.auth.Token;
import prm392.orderfood.domain.repositories.AuthRepository;
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
                        tokenLocalDataSource.saveToken(token.getAccessToken(), token.getRefreshToken(),
                                token.getUserRole(), token.getUserId());
                        return Response.success(token);
                    } else {
                        return Response.error(response.code(), response.errorBody());
                    }
                });
    }

    @Override
    public Completable logOut() {
        // Clear the local token before logging out
        return authDataSource.logOut()
                .andThen(Completable.fromAction(tokenLocalDataSource::clearToken))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Response<Boolean>> validateAccessToken() {
        return Single.fromCallable(() -> {
            try {
                String accessToken = tokenLocalDataSource.getAccessToken();
                boolean isValid = tokenLocalDataSource.isTokenValid(accessToken);
                return Response.success(isValid);
            } catch (Exception e) {
                return Response.error(500, ResponseBody.create("Token validation failed", (okhttp3.MediaType) null));            }
        });
    }
}
