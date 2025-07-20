package prm392.orderfood.androidapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import prm392.orderfood.domain.models.transactions.Transaction;
import prm392.orderfood.domain.usecase.TransactionUseCase;
import prm392.orderfood.androidapp.utils.SingleLiveEvent;
import retrofit2.Response;

@HiltViewModel
public class TransactionViewModel extends ViewModel {
    private final TransactionUseCase transactionUseCase;
    private final CompositeDisposable disposable;

    private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();
    private final SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public TransactionViewModel(TransactionUseCase transactionUseCase) {
        this.transactionUseCase = transactionUseCase;
        this.disposable = new CompositeDisposable();
    }

    public LiveData<List<Transaction>> getTransactionsLiveData() {
        return transactions;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    public void fetchTransactions(String userId) {
        isLoading.setValue(true);
        disposable.add(
                transactionUseCase.getAllTransactionsByUserId(userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    isLoading.setValue(false);
                                    if (response.isSuccessful() && response.body() != null) {
                                        List<Transaction> data = response.body();
                                        if (data != null && !data.isEmpty()) {
                                            transactions.setValue(data);
                                        } else {
                                            transactions.setValue(null); // báo về UI là không có dữ liệu
                                            toastMessage.setValue("No transactions found");
                                        }
                                    } else {
                                        errorMessage.setValue("Failed to load transactions: " + response.message());
                                    }
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to load transactions: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void requestWithdraw(String userId, double amount, String description) {
        isLoading.setValue(true);
        disposable.add(
                transactionUseCase.requestWithdraw(userId, amount, description)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    isLoading.setValue(false);
                                    if (response.isSuccessful()) {
                                        toastMessage.setValue("Withdraw request submitted successfully");
                                    } else {
                                        errorMessage.setValue("Withdraw request failed: " + response.message());
                                    }
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Withdraw request failed: " + throwable.getMessage());
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
