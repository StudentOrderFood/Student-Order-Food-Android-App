package prm392.orderfood.androidapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import dagger.hilt.android.AndroidEntryPoint;
import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.ActivityMainBinding;
import prm392.orderfood.androidapp.databinding.FragmentHomeBinding;
import prm392.orderfood.androidapp.ui.adapter.HomeCategoryAdapter;
import prm392.orderfood.androidapp.utils.DateTimeUtils;
import prm392.orderfood.androidapp.viewModel.CategoryViewModel;
import prm392.orderfood.androidapp.viewModel.UserViewModel;
import prm392.orderfood.domain.models.category.CategoryResponse;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private UserViewModel mUserViewModel;
    private CategoryViewModel mCategoryViewModel;

    private HomeCategoryAdapter homeCategoryAdapter;


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        mCategoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        setupObservers();
        setupEvents();
        setupRecyclerView();

        if (mUserViewModel.getUserProfileLiveData().getValue() == null) {
            // Chỉ gọi khi chưa có dữ liệu
            mUserViewModel.fetchUserProfile();
        }

        mCategoryViewModel.getAllCategories();

        Log.d(TAG, "onViewCreated: Current Time: " + DateTimeUtils.getCurrentTime());
    }

    private void setupObservers() {
        mUserViewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), userProfile -> {
            if (userProfile != null) {
                // Sử dụng Glide để tải ảnh đại diện
                Glide.with(requireContext())
                        .load(userProfile.getAvatar())
                        .placeholder(R.drawable.avatar)
                        .into(binding.civProfileImg);
            } else {
                // Xử lý trường hợp không có dữ liệu người dùng
                binding.civProfileImg.setImageResource(R.drawable.avatar); // Hoặc một hình ảnh mặc định khác
            }
        });

        mCategoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categoryList -> {
            if (categoryList != null && !categoryList.isEmpty()) {
                homeCategoryAdapter.updateData(categoryList);
            }
        });
    }
    private void setupEvents() {
        binding.civProfileImg.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_global_profileFragment);
        });
    }

    private void setupRecyclerView() {
        homeCategoryAdapter = new HomeCategoryAdapter(
                new java.util.ArrayList<>(),
                position -> {
                    CategoryResponse selected = mCategoryViewModel.getCategoriesLiveData().getValue().get(position);
                    // TODO: Xử lý khi nhấn vào category
                    // Ví dụ: load lại danh sách shop theo category
                }
        );
        binding.rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvCategories.setHasFixedSize(true);
        binding.rvCategories.setItemAnimator(new DefaultItemAnimator());
        binding.rvCategories.setAdapter(homeCategoryAdapter);
    }
}