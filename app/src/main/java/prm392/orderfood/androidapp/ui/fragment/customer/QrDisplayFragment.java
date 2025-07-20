package prm392.orderfood.androidapp.ui.fragment.customer;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import prm392.orderfood.androidapp.databinding.FragmentQrDisplayBinding;
import prm392.orderfood.androidapp.viewModel.OrderViewModel;

public class QrDisplayFragment extends Fragment {
    private final String TAG = "QrDisplayFragment";
    private FragmentQrDisplayBinding binding;
    private OrderViewModel mOrderViewModel;
    private NavController navController;
    private CountDownTimer countDownTimer;


    public QrDisplayFragment() {
        // Required empty public constructor
    }

    public static QrDisplayFragment newInstance(String param1, String param2) {
        QrDisplayFragment fragment = new QrDisplayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQrDisplayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOrderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        navController = Navigation.findNavController(requireView());

//        mOrderViewModel.getCheckoutUrlLiveData().observe(getViewLifecycleOwner(), chekoutUrl -> {
//            if (qrContent != null && !qrContent.getQrCode().isEmpty()) {
//                Bitmap bitmap = generateQrCode(qrContent.getQrCode());
//                binding.imgQrCode.setImageBitmap(bitmap);
//            }
//        });

        startCountdownTimer();

        mOrderViewModel.getErrorMessageLiveData()
                .observe(getViewLifecycleOwner(), errorMessage -> {
                    if (errorMessage != null && !errorMessage.isEmpty()) {
                        Log.e(TAG, "onViewCreated: " + errorMessage);
                    }
                });
    }

    private Bitmap generateQrCode(String content) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(2 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;
                binding.tvHint.setText("⏳ Mã QR sẽ hết hạn sau " + minutes + " phút " + remainingSeconds + " giây");
            }

            @Override
            public void onFinish() {
                binding.tvHint.setText("⚠️ Mã QR đã hết hạn");
                navController.popBackStack();
            }
        };
        countDownTimer.start();
    }
}