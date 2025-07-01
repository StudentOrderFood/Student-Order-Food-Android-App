package prm392.orderfood.data.repositoryImpl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.data.datasource.remote.ShopDataSource;
import prm392.orderfood.data.datasource.remote.modelRequest.shop.ApproveShopRequest;
import prm392.orderfood.data.datasource.remote.modelResponse.ApiResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.PagingResponse;
import prm392.orderfood.data.datasource.remote.modelResponse.shop.GetShopResponse;
import prm392.orderfood.data.mapper.ShopMapper;
import prm392.orderfood.domain.models.shops.Shop;
import prm392.orderfood.domain.repositories.ShopRepository;
import retrofit2.Response;

public class ShopRepositoryImpl implements ShopRepository {
    private final ShopDataSource dataSource;

    @Inject
    public ShopRepositoryImpl(ShopDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ShopOwner
    @Override
    public Single<Shop> createShop(Shop shop, File image, List<File> subImages) {
        return dataSource.createShop(
                        shop.getName(),
                        shop.getAddress(),
                        shop.getOpenHours(),
                        shop.getEndHours(),
                        image,
                        subImages
                )
                .map(Response::body)
                .map(ApiResponse::getData)
                .map(ShopMapper::toDomain) // mapping to domain
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Shop> updateShop(Shop shop, File image, List<File> subImages) {
        return dataSource.updateShop(
                        shop.getId(),
                        shop.getName(),
                        shop.getAddress(),
                        shop.getOpenHours(),
                        shop.getEndHours(),
                        image,
                        subImages
                )
                .map(Response::body)
                .map(ApiResponse::getData)
                .map(ShopMapper::toDomain)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<List<Shop>> getShopsByOwner(int pageIndex, int pageSize) {
        return dataSource.getShopsByOwner(pageIndex, pageSize)
                .map(Response::body)
                .map(ApiResponse::getData)
                .map(PagingResponse::getItems)
                .map(ShopMapper::toDomainList) // list mapping
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Shop> getShopById(String shopId) {
        return dataSource.getShopById(shopId)
                .map(Response::body)
                .map(ApiResponse::getData)
                .map(ShopMapper::toDomain)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteShop(String shopId) {
        return dataSource.deleteShop(shopId)
                .map(Response::isSuccessful)
                .subscribeOn(Schedulers.io());
    }

    // Admin
    @Override
    public Single<List<Shop>> getShopsByStatus(String status, int pageIndex, int pageSize) {
        return dataSource.getShopsByStatus(status, pageIndex, pageSize)
                .map(Response::body)
                .map(ApiResponse::getData)
                .map(PagingResponse::getItems)
                .map(ShopMapper::toDomainList)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> approveOrRejectShop(String shopId, boolean isApproved) {
        ApproveShopRequest request = new ApproveShopRequest(shopId, isApproved);
        return dataSource.approveOrRejectShop(request)
                .map(Response::isSuccessful)
                .subscribeOn(Schedulers.io());
    }
}
