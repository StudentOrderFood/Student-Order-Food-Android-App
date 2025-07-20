package prm392.orderfood.domain.repositories;

import java.util.List;

import io.reactivex.Single;
import prm392.orderfood.domain.models.transactions.Transaction;
import retrofit2.Response;

public interface TransactionRepository {
    Single<Response<List<Transaction>>> getAllTransactionsByUserId(String userId);
    Single<Response<Transaction>> requestWithdraw(String userId, double amount, String description);
}
