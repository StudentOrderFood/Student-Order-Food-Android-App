package prm392.orderfood.androidapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;
import prm392.orderfood.androidapp.utils.SingleLiveEvent;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import prm392.orderfood.domain.models.orderItem.OrderItem;
import prm392.orderfood.domain.usecase.UserUseCase;

@HiltViewModel
public class OrderViewModel extends ViewModel {
    private final CompositeDisposable mCompositeDisposable;

    @Inject
    public OrderViewModel() {
        this.mCompositeDisposable = new CompositeDisposable();
    }

    private final SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();

    private LiveData<String> getErrorMessageLiveData() {
        return errorMessage;
    }

    private final MutableLiveData<List<OrderItem>> orderItems = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<OrderItem>> getOrderItemsLiveData() {
        return orderItems;
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
}
