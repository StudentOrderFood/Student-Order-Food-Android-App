package prm392.orderfood.data.repositoryImpl;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import prm392.orderfood.data.datasource.remote.TransactionDataSource;
import prm392.orderfood.data.datasource.remote.modelRequest.transaction.WithdrawRequest;
import prm392.orderfood.domain.models.transactions.Transaction;
import prm392.orderfood.domain.repositories.TransactionRepository;
import retrofit2.Response;

public class TransactionRepositoryImpl implements TransactionRepository {
    private final TransactionDataSource dataSource;

    @Inject
    public TransactionRepositoryImpl(TransactionDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Single<Response<List<Transaction>>> getAllTransactionsByUserId(String userId) {
        return dataSource.getAllTransactionsByUserId(userId)
                .map(apiResponse -> {
                    if (apiResponse.isSuccessful() && apiResponse.body() != null) {
                        // Lấy data từ ApiResponse<T>
                        List<Transaction> data = apiResponse.body().getData();
                        return Response.success(data);
                    } else {
                        return Response.error(apiResponse.code(), apiResponse.errorBody());
                    }
                });
    }

    @Override
    public Single<Response<Transaction>> requestWithdraw(String userId, double amount, String description) {
        WithdrawRequest request = new WithdrawRequest(userId, amount, description);
        return dataSource.requestWithdraw(request)
                .map(apiResponse -> {
                    if (apiResponse.isSuccessful() && apiResponse.body() != null) {
                        Transaction data = apiResponse.body().getData();
                        return Response.success(data);
                    } else {
                        return Response.error(apiResponse.code(), apiResponse.errorBody());
                    }
                });
    }
}
