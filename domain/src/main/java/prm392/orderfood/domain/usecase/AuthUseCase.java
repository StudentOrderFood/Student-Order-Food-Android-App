package prm392.orderfood.domain.usecase;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import prm392.orderfood.domain.models.auth.Token;
import prm392.orderfood.domain.repositories.AuthRepository;
import retrofit2.Response;

public class AuthUseCase {
    private final AuthRepository authRepository;

    @Inject
    public AuthUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Single<Response<Token>> login() {
        return authRepository.logIn();
    }

    public Completable signOut() {
        return authRepository.logOut();
    }

    public  Single<Response<Boolean>> validateAccessToken() {
        return authRepository.validateAccessToken();
    }
}
