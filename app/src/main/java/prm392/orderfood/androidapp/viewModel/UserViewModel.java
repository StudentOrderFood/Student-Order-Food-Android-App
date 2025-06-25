package prm392.orderfood.androidapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import prm392.orderfood.domain.models.users.UserProfile;
import prm392.orderfood.domain.usecase.UserUseCase;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private final UserUseCase userUseCase;
    private final CompositeDisposable mCompositeDisposable;

    // MutableLiveData để lưu trữ thông tin người dùng
    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();

    // LiveData để quan sát thông tin người dùng
    public LiveData<UserProfile> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    @Inject
    public UserViewModel(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
        this.mCompositeDisposable = new CompositeDisposable();
    }

    // Phương thức để lấy thông tin người dùng
    public void fetchUserProfile() {
        Disposable disposable = userUseCase.getUserProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    response -> userProfileLiveData.setValue(response.body()),
                    throwable -> {
                        // Xử lý lỗi nếu cần
                        userProfileLiveData.setValue(null);
                    }
                );
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear(); // Giải phóng bộ nhớ khi ViewModel bị hủy
    }

}
