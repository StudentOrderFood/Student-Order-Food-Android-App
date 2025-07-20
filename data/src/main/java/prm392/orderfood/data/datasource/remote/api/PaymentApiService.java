package prm392.orderfood.data.datasource.remote.api;

import io.reactivex.Single;
import prm392.orderfood.domain.models.orders.BankingOrderRequest;
import prm392.orderfood.domain.models.payment.QrCodeResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentApiService {
    @POST("api/v1/Payments/create-qrcode")
    Single<QrCodeResponse> createPaymentQrCode(@Body BankingOrderRequest request);
}
