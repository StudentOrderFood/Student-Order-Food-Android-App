package prm392.orderfood.data.datasource.remote;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.remote.api.PaymentApiService;
import prm392.orderfood.domain.models.orders.BankingOrderRequest;
import prm392.orderfood.domain.models.payment.QrCodeResponse;

public class PaymentDataSource {
    private final PaymentApiService paymentApiService;

    @Inject
    public PaymentDataSource(PaymentApiService paymentApiService) {
        this.paymentApiService = paymentApiService;
    }

    public Single<QrCodeResponse> generatePaymentQrCode(BankingOrderRequest request) {
        return paymentApiService.createPaymentQrCode(request);
    }
}
