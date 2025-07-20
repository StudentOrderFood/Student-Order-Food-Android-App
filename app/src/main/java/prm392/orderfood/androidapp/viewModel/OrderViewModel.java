package prm392.orderfood.androidapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import prm392.orderfood.androidapp.utils.SingleLiveEvent;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import prm392.orderfood.domain.models.orderItem.OrderItem;
import prm392.orderfood.domain.models.orderItem.OrderItemModel;
import prm392.orderfood.domain.models.orders.BankingOrderRequest;
import prm392.orderfood.domain.models.orders.Order;
import prm392.orderfood.domain.models.orders.OrderRealTime;
import prm392.orderfood.domain.models.payment.QrCodeResponse;
import prm392.orderfood.domain.usecase.OrderUseCase;
import prm392.orderfood.domain.usecase.PaymentUseCase;

@HiltViewModel
public class OrderViewModel extends ViewModel {
    private final CompositeDisposable mCompositeDisposable;
    private final OrderUseCase orderUseCase;
    private final PaymentUseCase paymentUseCase;

    @Inject
    public OrderViewModel(OrderUseCase orderUseCase, PaymentUseCase paymentUseCase) {
        this.mCompositeDisposable = new CompositeDisposable();
        this.orderUseCase = orderUseCase;
        this.paymentUseCase = paymentUseCase;
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

    private MutableLiveData<QrCodeResponse> qrCodeLiveData = new MutableLiveData<>();
    public LiveData<QrCodeResponse> getQrCodeLiveData() {
        return qrCodeLiveData;
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

    public void generateQrCode(Order newOrder) {
        // Mapping newOrder to BankingOrderRequest
        BankingOrderRequest bankingOrderRequest = new BankingOrderRequest();
        bankingOrderRequest.setCustomerId(newOrder.getCustomerId());
        bankingOrderRequest.setShopId(newOrder.getShopId());
        bankingOrderRequest.setPaymentMethod(newOrder.getPaymentMethod());
        bankingOrderRequest.setTotalAmount(newOrder.getTotalAmount());
        bankingOrderRequest.setOrderStatus("Pending");

        // Mapping order items to OrderItemModels
        List<OrderItemModel> orderItemModels = new ArrayList<>();
        for (OrderItem item : newOrder.getOrderItems()) {
            OrderItemModel orderItemModel = new OrderItemModel();
            orderItemModel.setItemId(item.getItem().getId());
            orderItemModel.setQuantity(item.getQuantity());
            orderItemModel.setPrice(item.getPrice());
            orderItemModels.add(orderItemModel);
        }
        bankingOrderRequest.setOrderItems(orderItemModels);

        //Generate OrderCode;
        bankingOrderRequest.setPayosOrderCode(generate11DigitOrderCode());

        mCompositeDisposable.add(
                paymentUseCase.generatePaymentQrCode(bankingOrderRequest)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                qrCode -> {
                                    qrCodeLiveData.setValue(qrCode);
                                    orderItems.setValue(new ArrayList<>()); // Clear the cart after successful order
                                    toastMessage.setValue("QR Code generated successfully");
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to generate QR Code" + throwable.getMessage());
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

    private static long generate11DigitOrderCode() {
        Random random = new Random();
        long min = 100_000_000_00L; // Số nhỏ nhất có 11 chữ số
        long max = 999_999_999_99L; // Số lớn nhất có 11 chữ số
        return min + ((long)(random.nextDouble() * (max - min)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }
}
