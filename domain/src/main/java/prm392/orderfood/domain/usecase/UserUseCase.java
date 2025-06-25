package prm392.orderfood.domain.usecase;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.domain.models.users.UserProfile;
import prm392.orderfood.domain.repositories.UserRepository;
import retrofit2.Response;

public class UserUseCase {
    private final UserRepository userRepository;
    @Inject
    public UserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Single<Response<UserProfile>> getUserProfile() {
        return userRepository.getUserProfile()
                .subscribeOn(Schedulers.io());
    }
}
