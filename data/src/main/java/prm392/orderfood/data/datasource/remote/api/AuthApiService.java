package prm392.orderfood.data.datasource.remote.api;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.remote.modelRequest.IdTokenRequest;
import prm392.orderfood.data.datasource.remote.modelResponse.auth.TokenResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("api/v1/Authentications/student-login")
    Single<Response<TokenResponse>> sendIdToken(@Body IdTokenRequest idToken);

}
