package prm392.orderfood.data.datasource.remote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import prm392.orderfood.data.datasource.remote.api.ShopApiService;
import prm392.orderfood.data.datasource.remote.modelRequest.shop.ApproveShopRequest;
import prm392.orderfood.data.datasource.remote.modelResponse.ApiResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.PagingResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.shop.GetShopDetailResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.shop.GetShopResponse;
import prm392.orderfood.domain.models.shops.PopularShopResponse;
import retrofit2.Response;

public class ShopDataSource {
    private final ShopApiService api;

    @Inject
    public ShopDataSource(ShopApiService api) {
        this.api = api;
    }

    private RequestBody toPart(String value) {
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }

    private MultipartBody.Part toMultipartBody(String name, File file) {
        RequestBody req = RequestBody.create(file, MediaType.parse("image/*"));
        return MultipartBody.Part.createFormData(name, file.getName(), req);
    }

    public Single<Response<ApiResponse<GetShopResponse>>> createShop(
            String name,
            String address,
            String openHours,
            String endHours,
            File image,
            List<File> subImages
    ) {
        List<MultipartBody.Part> additionalParts = new ArrayList<>();
        if (subImages != null) {
            for (File img : subImages) {
                additionalParts.add(toMultipartBody("additionalImages", img));
            }
        }

        return api.createShop(
                toPart(name),
                toPart(address),
                toPart(openHours),
                toPart(endHours),
                toMultipartBody("image", image),
                additionalParts
        );
    }

    public Single<Response<ApiResponse<GetShopResponse>>> updateShop(
            String shopId,
            String name,
            String address,
            String openHours,
            String endHours,
            File image, // may be null
            List<File> subImages // may be null
    ) {
        MultipartBody.Part imagePart = null;
        if (image != null) {
            imagePart = toMultipartBody("image", image);
        }

        List<MultipartBody.Part> additionalParts = new ArrayList<>();
        if (subImages != null) {
            for (File img : subImages) {
                additionalParts.add(toMultipartBody("additionalImages", img));
            }
        }

        return api.updateShop(
                toPart(shopId),
                toPart(name),
                toPart(address),
                toPart(openHours),
                toPart(endHours),
                imagePart,
                additionalParts
        );
    }

    public Single<Response<ApiResponse<String>>> deleteShop(String shopId) {
        return api.deleteShop(shopId);
    }

    public Single<Response<ApiResponse<PagingResponse<GetShopResponse>>>> getShopsByOwner(int pageIndex, int pageSize) {
        return api.getShopsByOwner(pageIndex, pageSize);
    }

    public Single<Response<ApiResponse<GetShopResponse>>> getShopById(String shopId) {
        return api.getShopById(shopId);
    }

    public Single<Response<ApiResponse<PagingResponse<GetShopResponse>>>> getShopsByStatus(String status, int pageIndex, int pageSize) {
        return api.getShopsByStatus(status, pageIndex, pageSize);
    }

    public Single<Response<ApiResponse<String>>> approveOrRejectShop(ApproveShopRequest request) {
        return api.approveOrRejectShop(request);
    }

    public Single<Response<ApiResponse<GetShopDetailResponse>>> getShopDetail(String shopId) {
        return api.getShopDetail(shopId);
    }

    public Single<ApiResponse<List<PopularShopResponse>>> getPopularShops(String curTime) {
        return api.getPopularShops(curTime);
    }
}
