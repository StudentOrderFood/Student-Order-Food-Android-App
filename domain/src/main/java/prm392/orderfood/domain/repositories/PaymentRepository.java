package prm392.orderfood.domain.repositories;

import io.reactivex.Single;
import prm392.orderfood.domain.models.orders.BankingOrderRequest;
import prm392.orderfood.domain.models.payment.QrCodeResponse;

public interface PaymentRepository {
     Single<QrCodeResponse> generatePaymentQrCode(BankingOrderRequest request);
}
