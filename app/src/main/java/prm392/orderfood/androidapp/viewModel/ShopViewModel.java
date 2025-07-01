package prm392.orderfood.androidapp.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import prm392.orderfood.domain.models.shops.Shop;
import prm392.orderfood.domain.usecase.ShopUseCase;

@HiltViewModel
public class ShopViewModel extends ViewModel {

    private final ShopUseCase shopUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Shop>> _shops = new MutableLiveData<>();
    public LiveData<List<Shop>> shops = _shops;

    private final MutableLiveData<Shop> _shopDetail = new MutableLiveData<>();
    public LiveData<Shop> shopDetail = _shopDetail;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    public LiveData<Boolean> loading = _loading;

    private final MutableLiveData<Boolean> _actionSuccess = new MutableLiveData<>();
    public LiveData<Boolean> actionSuccess = _actionSuccess;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    @Inject
    public ShopViewModel(ShopUseCase shopUseCase) {
        this.shopUseCase = shopUseCase;
    }

    public void loadShopsByStatus(String status, int pageIndex, int pageSize) {
        _loading.setValue(true);
        disposables.add(
                shopUseCase.getShopsByStatus(status, pageIndex, pageSize)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                shops -> {
                                    _shops.setValue(shops);
                                    _loading.setValue(false);
                                },
                                error -> handleError("Load shops by status", error)
                        )
        );
    }

    public void loadShopsByOwner(int pageIndex, int pageSize) {
        _loading.setValue(true);
        disposables.add(
                shopUseCase.getShopsByOwner(pageIndex, pageSize)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                shops -> {
                                    _shops.setValue(shops);
                                    _loading.setValue(false);
                                },
                                error -> handleError("Load shops by owner", error)
                        )
        );
    }

    public void getShopById(String shopId) {
        _loading.setValue(true);
        disposables.add(
                shopUseCase.getShopById(shopId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                shop -> {
                                    _shopDetail.setValue(shop);
                                    _loading.setValue(false);
                                },
                                error -> handleError("Get shop by ID", error)
                        )
        );
    }

    public void createShop(Shop shop, File image, List<File> subImages) {
        _loading.setValue(true);
        disposables.add(
                shopUseCase.createShop(shop, image, subImages)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                createdShop -> {
                                    _shopDetail.setValue(createdShop);
                                    _actionSuccess.setValue(true);
                                    _loading.setValue(false);
                                },
                                error -> handleError("Create shop", error)
                        )
        );
    }

    public void updateShop(Shop shop, File image, List<File> subImages) {
        _loading.setValue(true);
        disposables.add(
                shopUseCase.updateShop(shop, image, subImages)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                updatedShop -> {
                                    _shopDetail.setValue(updatedShop);
                                    _actionSuccess.setValue(true);
                                    _loading.setValue(false);
                                },
                                error -> handleError("Update shop", error)
                        )
        );
    }

    public void deleteShop(String shopId) {
        _loading.setValue(true);
        disposables.add(
                shopUseCase.deleteShop(shopId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                success -> {
                                    _actionSuccess.setValue(success);
                                    _loading.setValue(false);
                                },
                                error -> handleError("Delete shop", error)
                        )
        );
    }

    public void approveOrRejectShop(String shopId, boolean isApproved) {
        _loading.setValue(true);
        disposables.add(
                shopUseCase.approveOrRejectShop(shopId, isApproved)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                success -> {
                                    _actionSuccess.setValue(success);
                                    _loading.setValue(false);
                                },
                                error -> handleError("Approve/Reject shop", error)
                        )
        );
    }

    private void handleError(String source, Throwable error) {
        Log.e("ShopViewModel", source + " failed", error);
        _errorMessage.setValue(source + " failed: " + error.getMessage());
        _loading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
