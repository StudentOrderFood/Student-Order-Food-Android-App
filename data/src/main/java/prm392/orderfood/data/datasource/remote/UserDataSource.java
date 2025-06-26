package prm392.orderfood.data.datasource.remote;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.remote.api.UserApiService;
import prm392.orderfood.data.datasource.remote.modelResponse.ApiResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.user.GetUserResponse;
import retrofit2.Response;

public class UserDataSource {
    private final UserApiService userApiService;

    @Inject
    public UserDataSource(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public Single<ApiResponse<GetUserResponse>> getUserById(String userId) {
        return userApiService.getUserProfile(userId);
    }
}
