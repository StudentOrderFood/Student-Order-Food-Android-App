package prm392.orderfood.domain.usecase;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.domain.models.orders.BankingOrderRequest;
import prm392.orderfood.domain.models.payment.QrCodeResponse;
import prm392.orderfood.domain.repositories.PaymentRepository;

public class PaymentUseCase {
    private final PaymentRepository paymentRepository;

    @Inject
    public PaymentUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Single<QrCodeResponse> generatePaymentQrCode(BankingOrderRequest request) {
        return paymentRepository.generatePaymentQrCode(request)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io());
    }
}
