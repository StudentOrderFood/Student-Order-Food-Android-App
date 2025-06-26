package prm392.orderfood.androidapp.ui.fragment.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.FragmentProfileMenuBinding;
import prm392.orderfood.androidapp.viewModel.AuthViewModel;
import prm392.orderfood.androidapp.viewModel.UserViewModel;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private FragmentProfileMenuBinding binding;
    private UserViewModel mUserViewModel;
    private AuthViewModel mAuthViewModel;
    NavController navController;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        navController = NavHostFragment.findNavController(this);
        setupObservers();
        setupEvents();

        if (mUserViewModel.getUserProfileLiveData().getValue() == null) {
            // Chỉ gọi khi chưa có dữ liệu
            mUserViewModel.fetchUserProfile();
        }
    }

    private void setupObservers() {
        mUserViewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), userProfile -> {
            if (userProfile != null) {
                binding.tvUserName.setText(userProfile.getFullName());
                binding.tvUserBio.setText("@" + userProfile.getEmail().split("@")[0]);
                // Load ảnh bằng Glide (bạn cần thêm thư viện Glide vào nếu chưa có)
                Glide.with(this)
                        .load(userProfile.getAvatar()) // phải là URL hoặc uri hợp lệ
                        .placeholder(R.drawable.avatar) // fallback nếu không có ảnh
                        .into(binding.ivUserAvatar);
            }
        });
    }

    private void setupEvents() {
//        binding.ivBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        binding.ivBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());
        binding.llLogOut.setOnClickListener(v -> {
            mAuthViewModel.logout(() -> {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true) // Clear toàn bộ stack
                        .build();

                NavHostFragment.findNavController(this).navigate(
                        R.id.loginFragment,
                        null,
                        navOptions
                );

                Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            });
        });

    }
}
