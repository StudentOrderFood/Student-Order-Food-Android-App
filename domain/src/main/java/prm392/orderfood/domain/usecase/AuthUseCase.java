package prm392.orderfood.domain.usecase;

import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.domain.models.Token;
import prm392.orderfood.domain.models.User;
import prm392.orderfood.domain.repositories.auth.AuthRepository;
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

    public User getCurrentUser() {
        return authRepository.getCurrentUser();
    }
    public Task<Response<Void>> SignOut() {
        return authRepository.logOut();
    }
}
