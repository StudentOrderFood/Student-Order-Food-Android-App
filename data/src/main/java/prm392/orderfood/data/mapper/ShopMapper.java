package prm392.orderfood.data.mapper;

import java.util.ArrayList;
import java.util.List;

import prm392.orderfood.data.datasource.remote.modelResponse.shop.GetShopResponse;
import prm392.orderfood.domain.models.shops.Shop;

public class ShopMapper {

    public static Shop toDomain(GetShopResponse response) {
        if (response == null) return null;

        Shop shop = new Shop();
        shop.setId(response.getId());
        shop.setName(response.getName());
        shop.setAddress(response.getAddress());
        shop.setImageUrl(response.getImageUrl());
        shop.setOpenHours(response.getOpenHours());
        shop.setEndHours(response.getEndHours());
        shop.setRating(response.getRating());
        shop.setStatus(response.getStatus());
        shop.setOwnerId(response.getOwnerId());
        shop.setImages(response.getImages()); // assuming this is a list of string URLs

        return shop;
    }

    public static List<Shop> toDomainList(List<GetShopResponse> responseList) {
        List<Shop> shops = new ArrayList<>();
        if (responseList != null) {
            for (GetShopResponse response : responseList) {
                shops.add(toDomain(response));
            }
        }
        return shops;
    }
}
