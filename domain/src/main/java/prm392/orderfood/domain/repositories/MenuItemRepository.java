package prm392.orderfood.domain.repositories;

import java.io.File;

import io.reactivex.Single;
import prm392.orderfood.domain.models.menuItem.MenuItem;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import retrofit2.Response;

public interface MenuItemRepository {
    Single<Response<MenuItemResponse>> createMenuItem(MenuItem menuItem, File img);
}
