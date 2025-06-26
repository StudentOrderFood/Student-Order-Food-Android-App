package prm392.orderfood.data.repositoryImpl;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.local.TokenLocalDataSource;
import prm392.orderfood.data.datasource.remote.UserDataSource;
import prm392.orderfood.data.mapper.UserMapper;
import prm392.orderfood.domain.models.users.UserProfile;
import prm392.orderfood.domain.repositories.UserRepository;
import retrofit2.Response;

public class UserRepositoryImpl implements UserRepository {
    private final UserDataSource userDataSource;
    private final TokenLocalDataSource tokenLocalDataSource;

    @Inject
    public UserRepositoryImpl(UserDataSource userDataSource, TokenLocalDataSource tokenLocalDataSource) {
        this.userDataSource = userDataSource;
        this.tokenLocalDataSource = tokenLocalDataSource;
    }
    @Override
    public Single<Response<UserProfile>> getUserProfile() {
        String userId = tokenLocalDataSource.getUserId();
        return userDataSource.getUserById(userId)
                .map(response -> {
                    boolean success = response.isSuccess();
                    if (success) {
                        return Response.success(UserMapper.mapToUserProfileDomain(response.getData()));
                    } else {
                        String errorMessage = response.getMessage() != null ? response.getMessage() : "Failed to fetch user profile";
                        return Response.error(400, okhttp3.ResponseBody.create(errorMessage, null));
                    }
                });
    }
}
