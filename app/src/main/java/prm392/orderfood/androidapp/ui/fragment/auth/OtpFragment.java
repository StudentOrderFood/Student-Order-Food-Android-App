package prm392.orderfood.androidapp.ui.fragment.auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.FragmentOtpBinding;
import prm392.orderfood.androidapp.databinding.FragmentPhoneInputBinding;
import prm392.orderfood.androidapp.viewModel.AuthViewModel;

public class OtpFragment extends Fragment {
    private static final String TAG = "OtpFragment";
    private FragmentOtpBinding binding;
    private AuthViewModel mAuthViewModel;

    private NavController navController;


    public OtpFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static OtpFragment newInstance(String param1, String param2) {
        OtpFragment fragment = new OtpFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize ViewModel
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        navController = NavHostFragment.findNavController(this);        // Enable the button to send OTP
        binding.btnSend.setEnabled(true);

        binding.btnSend.setOnClickListener(v -> {
            // Validate OTP input
            String otpCode = binding.otpView.getText().toString().trim();
            if (otpCode.length() < 6) {
                Toast.makeText(requireContext(), "Please enter a valid OTP code", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuthViewModel.verifyOtpCode(otpCode);
            // Disable the button to prevent multiple clicks
            binding.btnSend.setEnabled(false);
        });

        binding.btnResendOtp.setOnClickListener(v -> {
            mAuthViewModel.resendOtp(requireActivity());
        });

        mAuthViewModel.otpVerified.observe(getViewLifecycleOwner(), verified -> {
            if (Boolean.TRUE.equals(verified)) {
                Toast.makeText(requireContext(), "Verified successfully. Please login again", Toast.LENGTH_SHORT).show();
                navController.popBackStack(R.id.loginFragment, false);
                mAuthViewModel.otpVerified.setValue(null);
            }
        });

        mAuthViewModel.error.observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            // Re-enable the button if there was an error
            binding.btnSend.setEnabled(true);
        });
    }
    @Override
    public void onResume() {
        super.onResume();

        Log.d("BackStack", "Fragments in back stack:");
        for (Fragment fragment : requireActivity().getSupportFragmentManager().getFragments()) {
            if (fragment != null && fragment.isVisible()) {
                Log.d("BackStack", "Visible Fragment: " + fragment.getClass().getSimpleName());
            }
        }
    }
}