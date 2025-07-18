package prm392.orderfood.androidapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.ActivityMainBinding;
import prm392.orderfood.androidapp.databinding.FragmentHomeBinding;
import prm392.orderfood.androidapp.ui.adapter.HomeCategoryAdapter;
import prm392.orderfood.androidapp.ui.adapter.PopularShopAdapter;
import prm392.orderfood.androidapp.utils.DateTimeUtils;
import prm392.orderfood.androidapp.viewModel.CategoryViewModel;
import prm392.orderfood.androidapp.viewModel.ShopViewModel;
import prm392.orderfood.androidapp.viewModel.UserViewModel;
import prm392.orderfood.domain.models.category.CategoryResponse;
import prm392.orderfood.domain.models.shops.PopularShopResponse;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private UserViewModel mUserViewModel;
    private ShopViewModel mShopViewModel;
    private CategoryViewModel mCategoryViewModel;
    private NavController navController;

    private HomeCategoryAdapter homeCategoryAdapter;
    private PopularShopAdapter popularShopAdapter;

    private List<PopularShopResponse> fullShopList;
    private List<CategoryResponse> fullCategoryList;


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
        mShopViewModel = new ViewModelProvider(requireActivity()).get(ShopViewModel.class);
        navController = Navigation.findNavController(requireView());

        fullShopList = new ArrayList<>();
        fullCategoryList = new ArrayList<>();

        setupObservers();
        setupEvents();
        setupRecyclerView();

        if (mUserViewModel.getUserProfileLiveData().getValue() == null) {
            // Chỉ gọi khi chưa có dữ liệu
            mUserViewModel.fetchUserProfile();
        }

        mCategoryViewModel.getAllCategories();
        mShopViewModel.fetchPopularShops(DateTimeUtils.getCurrentTime());
//        Log.d(TAG, "onViewCreated: Current Time: " + DateTimeUtils.getCurrentTime());
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
                fullCategoryList = categoryList;
                fullCategoryList.add(0, new CategoryResponse("ALL", "All"));
                homeCategoryAdapter.updateData(categoryList);
            }
        });

        mShopViewModel.getPopularShopResponse().observe(getViewLifecycleOwner(), popularShopResponse -> {
//            Toast.makeText(requireContext(), "Popular shops loaded", Toast.LENGTH_SHORT).show();
            if (popularShopResponse != null) {
                fullShopList = popularShopResponse;
                popularShopAdapter.updateData(fullShopList);
            }
        });
    }
    private void setupEvents() {
        binding.civProfileImg.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_global_profileFragment);
        });
    }

    private void setupRecyclerView() {
        // Thiết lập RecyclerView cho danh sách category
        homeCategoryAdapter = new HomeCategoryAdapter(
                fullCategoryList,
                position -> {
                    CategoryResponse selected = mCategoryViewModel.getCategoriesLiveData().getValue().get(position);
                    if (selected == null || selected.getId() == null) return;
                    String selectedCategoryId = selected.getId();
                    if ("ALL".equalsIgnoreCase(selectedCategoryId)) {
                        // Nếu là "ALL", hiển thị toàn bộ
                        popularShopAdapter.updateData(fullShopList);
                    } else {
                        // Lọc theo categoryId
                        List<PopularShopResponse> filteredShops = new ArrayList<>();
                        for (PopularShopResponse shop : fullShopList) {
                            if (shop.getCategoryIds() != null && shop.getCategoryIds().contains(selectedCategoryId)) {
                                filteredShops.add(shop);
                            }
                        }
                        popularShopAdapter.updateData(filteredShops);
                    }
                }
        );
        binding.rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvCategories.setHasFixedSize(true);
        binding.rvCategories.setItemAnimator(new DefaultItemAnimator());
        binding.rvCategories.setAdapter(homeCategoryAdapter);

        // Thiết lập RecyclerView cho danh sách shop phổ biến
        popularShopAdapter = new PopularShopAdapter(
                fullShopList,
                shop -> {
                    mShopViewModel.setSelectedShop(shop.getId());
                    navController.navigate(R.id.action_homeFragment_to_shopDetailFragment);
                }
        );
        binding.rvPopularShops.setLayoutManager(
                new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        );
        binding.rvPopularShops.setHasFixedSize(true);
        binding.rvPopularShops.setItemAnimator(new DefaultItemAnimator());
        binding.rvPopularShops.setAdapter(popularShopAdapter);
    }
}