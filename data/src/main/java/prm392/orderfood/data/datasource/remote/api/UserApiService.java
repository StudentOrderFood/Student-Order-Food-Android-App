package prm392.orderfood.data.datasource.remote.api;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.remote.modelResponse.ApiResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.user.GetUserResponse;
import retrofit2.Response;
import retrofit2.http.*;
public interface UserApiService {
    @GET("api/v1/Users/{userId}")
    Single<ApiResponse<GetUserResponse>> getUserProfile(@Path("userId") String userId);
}