package prm392.orderfood.data.datasource.remote.api;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import prm392.orderfood.data.datasource.remote.modelResponse.ApiResponse;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MenuItemApiService {
    @Multipart
    @POST("api/v1/MenuItem")
    Single<ApiResponse<MenuItemResponse>> createMenuItem(
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part("imageUrl") RequestBody imageUrl,
            @Part("isAvailable") RequestBody isAvailable,
            @Part("shopId") RequestBody shopId,
            @Part("categoryId") RequestBody categoryId,
            @Part MultipartBody.Part image // nullable
    );
}
