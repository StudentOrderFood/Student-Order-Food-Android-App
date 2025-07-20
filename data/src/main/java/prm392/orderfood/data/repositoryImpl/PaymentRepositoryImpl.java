package prm392.orderfood.data.repositoryImpl;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.remote.PaymentDataSource;
import prm392.orderfood.domain.models.orders.BankingOrderRequest;
import prm392.orderfood.domain.models.payment.QrCodeResponse;
import prm392.orderfood.domain.repositories.PaymentRepository;

public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentDataSource paymentDataSource;

    @Inject
    public PaymentRepositoryImpl(PaymentDataSource paymentDataSource) {
        this.paymentDataSource = paymentDataSource;
    }

    @Override
    public Single<QrCodeResponse> generatePaymentQrCode(BankingOrderRequest request) {
        return paymentDataSource.generatePaymentQrCode(request);
    }
}
