package prm392.orderfood.domain.usecase;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.domain.models.menuItem.MenuItem;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import prm392.orderfood.domain.repositories.MenuItemRepository;
import retrofit2.Response;

public class MenuItemUseCase {
    private final MenuItemRepository menuItemRepository;

    @Inject
    public MenuItemUseCase(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public Single<Response<MenuItemResponse>> createMenuItem(MenuItem menuItem, File img) {
        return menuItemRepository.createMenuItem(menuItem, img)
                .subscribeOn(Schedulers.io());
    }

    public Single<MenuItemResponse> updateMenuItem(String id, MenuItem menuItem, File img) {
        return menuItemRepository.updateMenuItem(id, menuItem, img)
                .subscribeOn(Schedulers.io());
    }

    public Single<String> deleteMenuItem(String menuItemId) {
        return menuItemRepository.deleteMenuItem(menuItemId)
                .subscribeOn(Schedulers.io());
    }

}
