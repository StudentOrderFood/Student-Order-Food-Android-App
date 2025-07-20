package prm392.orderfood.androidapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import prm392.orderfood.androidapp.utils.SingleLiveEvent;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import prm392.orderfood.domain.models.orderItem.OrderItem;
import prm392.orderfood.domain.models.orders.Order;
import prm392.orderfood.domain.models.orders.OrderRealTime;
import prm392.orderfood.domain.usecase.OrderUseCase;

@HiltViewModel
public class OrderViewModel extends ViewModel {
    private final CompositeDisposable mCompositeDisposable;
    private final OrderUseCase orderUseCase;

    @Inject
    public OrderViewModel(OrderUseCase orderUseCase) {
        this.mCompositeDisposable = new CompositeDisposable();
        this.orderUseCase = orderUseCase;
    }

    private final SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessage;
    }

    private final MutableLiveData<List<OrderItem>> orderItems = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<OrderItem>> getOrderItemsLiveData() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> newList) {
        orderItems.setValue(newList);
    }

    private MutableLiveData<List<OrderRealTime>> ordersByShopId = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<OrderRealTime>> getOrderByShopIdLiveData() {
        return ordersByShopId;
    }

    public void addItemToOrder(MenuItemResponse menuItem, int quantity) {
        List<OrderItem> currentItems = orderItems.getValue();
        if (currentItems == null) {
            errorMessage.setValue("Failed to add " + menuItem.getName() + " to cart");
            return;
        }

        if (updateExistingItemQuantity(currentItems, menuItem, quantity)) {
            toastMessage.setValue("Added " + quantity + " " + menuItem.getName() + " to cart successfully");
        } else {
            addNewItemToCart(currentItems, menuItem, quantity);
            toastMessage.setValue("Added " + quantity + " " + menuItem.getName() + " to cart successfully");
        }

        orderItems.setValue(currentItems); // notify observers
    }

    public void submitCodOrder(Order newOrder) {
        newOrder.setOrderStatus("Pending");
        mCompositeDisposable.add(
                orderUseCase.submitCodOrder(newOrder)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    toastMessage.setValue("Order submitted successfully");
                                    orderItems.setValue(new ArrayList<>()); // Clear the cart after successful order
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to submit order: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void getOrdersByShopId(String shopId) {
        mCompositeDisposable.add(
                orderUseCase.getOrdersByShopId(shopId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                orders -> {
                                    ordersByShopId.setValue(orders);
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to fetch orders: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        mCompositeDisposable.add(
                orderUseCase.updateOrderStatus(orderId, newStatus)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                },
                                throwable -> {
                                }
                        )
        );
    }

    private boolean updateExistingItemQuantity(List<OrderItem> currentItems, MenuItemResponse menuItem, int quantity) {
        for (OrderItem item : currentItems) {
            if (item.getItem().getId().equals(menuItem.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return true;
            }
        }
        return false;
    }

    private void addNewItemToCart(List<OrderItem> currentItems, MenuItemResponse menuItem, int quantity) {
        OrderItem newItem = new OrderItem(menuItem, quantity, menuItem.getPrice());
        currentItems.add(newItem);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }
}
