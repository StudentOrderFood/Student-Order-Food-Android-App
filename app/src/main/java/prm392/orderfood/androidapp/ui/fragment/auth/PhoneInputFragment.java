package prm392.orderfood.androidapp.ui.fragment.auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.FragmentPhoneInputBinding;
import prm392.orderfood.androidapp.utils.PhoneNumberUtils;
import prm392.orderfood.androidapp.viewModel.AuthViewModel;

public class PhoneInputFragment extends Fragment {
    private static final String TAG = "PhoneInputFragment";
    private FragmentPhoneInputBinding binding;
    private AuthViewModel mAuthViewModel;

    private NavController navController;

    public PhoneInputFragment() {
        // Required empty public constructor
    }

    public static PhoneInputFragment newInstance() {
        PhoneInputFragment fragment = new PhoneInputFragment();
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
        binding = FragmentPhoneInputBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        navController = Navigation.findNavController(requireView());


        binding.btnSendOtp.setOnClickListener(v -> {
            String phoneNumber = binding.etPhone.getText().toString().trim();
            if (!PhoneNumberUtils.isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(requireContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuthViewModel.startPhoneVerification(phoneNumber, requireActivity());
        });

        mAuthViewModel.otpSent.observe(getViewLifecycleOwner(), sent -> {
            if (sent) {
                navController.navigate(R.id.action_phoneInputFragment_to_otpFragment);
            }
            mAuthViewModel.otpSent.setValue(false); // Reset the state after navigation
        });

        mAuthViewModel.error.observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });

    }
}