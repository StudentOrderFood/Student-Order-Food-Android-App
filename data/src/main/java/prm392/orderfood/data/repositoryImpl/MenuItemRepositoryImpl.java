package prm392.orderfood.data.repositoryImpl;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.remote.MenuItemDataSource;
import prm392.orderfood.domain.models.menuItem.MenuItem;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import prm392.orderfood.domain.repositories.MenuItemRepository;
import retrofit2.Response;

public class MenuItemRepositoryImpl implements MenuItemRepository {
    private final MenuItemDataSource menuItemDataSource;

    @Inject
    public MenuItemRepositoryImpl(MenuItemDataSource menuItemDataSource) {
        this.menuItemDataSource = menuItemDataSource;
    }

    @Override
    public Single<Response<MenuItemResponse>> createMenuItem(MenuItem menuItem, File img) {
        return menuItemDataSource.createMenuItem(menuItem, img)
                .map(response -> {
                    if(response.isSuccess()) {
                        return Response.success(response.getData());
                    } else {
                        String errorMessage = response.getMessage() != null ? response.getMessage() : "Failed to create menu item";
                        return Response.error(400, okhttp3.ResponseBody.create(errorMessage, null));
                    }
                });
    }
}
