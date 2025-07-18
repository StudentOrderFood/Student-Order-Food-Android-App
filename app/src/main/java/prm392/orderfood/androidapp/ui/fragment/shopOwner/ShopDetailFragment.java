package prm392.orderfood.androidapp.ui.fragment.shopOwner;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import dagger.hilt.android.AndroidEntryPoint;
import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.FragmentDetailShopBinding;
import prm392.orderfood.androidapp.ui.adapter.CategoryAdapter;
import prm392.orderfood.androidapp.ui.adapter.MenuItemAdapter;
import prm392.orderfood.androidapp.utils.CurrencyUtils;
import prm392.orderfood.androidapp.viewModel.AuthViewModel;
import prm392.orderfood.androidapp.viewModel.ShopViewModel;
import prm392.orderfood.domain.models.category.CategoriesInShopMenu;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;

@AndroidEntryPoint
public class ShopDetailFragment extends Fragment {

    public static final String TAG = "DetailShopFragment";
    private FragmentDetailShopBinding binding;
    private ShopViewModel mShopViewModel;
    private AuthViewModel mAuthViewModel;
    private NavController navController;

    private RecyclerView recyclerCategory;
    private RecyclerView recyclerMenuItem;

    private MenuItemAdapter menuItemAdapter;
    private CategoryAdapter categoryAdapter;

    private int quantity = 1;

    public ShopDetailFragment() {
        // Required empty public constructor
    }

    public static ShopDetailFragment newInstance(String param1, String param2) {
        ShopDetailFragment fragment = new ShopDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShopViewModel = new ViewModelProvider(requireActivity()).get(ShopViewModel.class);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        navController = Navigation.findNavController(requireView());
        recyclerCategory = binding.recyclerCategory;
        recyclerMenuItem = binding.recyclerMenuItems;
        setUpObservers();
        setUpEvents();

        // Mặc định ẩn thông tin sản phẩm đã chọn
        binding.layoutSelectedProductInfo.setVisibility(View.GONE);
        binding.btnPlus.setVisibility(View.GONE);
        binding.btnMinus.setVisibility(View.GONE);

        if ("ShopOwner".equalsIgnoreCase(mAuthViewModel.getUserRole().getValue())) {
            binding.layoutAddToCart.setVisibility(View.GONE);
        }
    }

    private void setUpEvents() {
        // Set up click listeners for back button
        binding.ivBack.setOnClickListener(v -> {
            navController.popBackStack();
        });

        // Set up click listener for add product button
        binding.btnAddProd.setOnClickListener(v -> {
            // Navigate to add product screen
            navController.navigate(R.id.action_shopDetailFragment_to_addProductFragment);
        });

        binding.btnPlus.setOnClickListener(v -> {
            quantity++;
            binding.tvQuantity.setText(String.valueOf(quantity));
            updateTotalPrice(CurrencyUtils.parseVNDToRaw(binding.tvSelectedProductPrice.getText().toString()));
        });

        binding.btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice(CurrencyUtils.parseVNDToRaw(binding.tvSelectedProductPrice.getText().toString()));
            }
        });

        binding.layoutSelectedProductInfo.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Selected product: " + binding.tvSelectedProductName.getText(), Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUpObservers() {
        mShopViewModel.getSelectedShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop == null) {
                Log.e(TAG, "Selected shop is null");
                Toast.makeText(getContext(), "No shop selected, cannot fetch shop detail", Toast.LENGTH_SHORT).show();
                return;
            }

            mShopViewModel.getShopDetail(shop);
        });

        mShopViewModel.getShopDetailResponse().observe(getViewLifecycleOwner(), shopDetailResponse -> {
            if (shopDetailResponse == null) {
                Log.e(TAG, "Shop detail response is null");
                Toast.makeText(getContext(), "Failed to fetch shop details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Loading BG Shop
            Glide.with(binding.getRoot())
                    .load(shopDetailResponse.getImageUrl())
                    .placeholder(R.drawable.sample_shop)
                    .into(binding.ivShopBG);

            // Loading Logo Shop
            Glide.with(binding.getRoot())
                    .load(shopDetailResponse.getImageUrl())
                    .placeholder(R.drawable.sample_shop)
                    .into(binding.ivShopLogo);

            // Set shop name
            binding.tvShopName.setText(shopDetailResponse.getName());
            // set shop rating
            binding.tvShopRating.setText(String.valueOf(shopDetailResponse.getRating()));

            // Set Open/End Hours
            try {
                // Giả sử dữ liệu ban đầu có định dạng "HH:mm:ss"
                SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                Date openTime = inputFormat.parse(shopDetailResponse.getOpenHours());
                Date endTime = inputFormat.parse(shopDetailResponse.getEndHours());

                String formattedOpenTime = outputFormat.format(openTime);
                String formattedEndTime = outputFormat.format(endTime);

                binding.tvOpenTime.setText(formattedOpenTime + " - " + formattedEndTime);
            } catch (Exception e) {
                Log.e(TAG, "setUpObservers: " + e.getMessage());
                binding.tvOpenTime.setText(shopDetailResponse.getOpenHours() + " - " + shopDetailResponse.getEndHours());
            }
            // Xử lý thời gian mở cửa và đóng cửa
            Calendar calendar = Calendar.getInstance();
            int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
            int nowMinute = calendar.get(Calendar.MINUTE);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            try {
                Date openTime = sdf.parse(shopDetailResponse.getOpenHours());
                Date endTime = sdf.parse(shopDetailResponse.getEndHours());

                Calendar openCal = Calendar.getInstance();
                openCal.setTime(openTime);
                int openHour = openCal.get(Calendar.HOUR_OF_DAY);
                int openMinute = openCal.get(Calendar.MINUTE);

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endTime);
                int endHour = endCal.get(Calendar.HOUR_OF_DAY);
                int endMinute = endCal.get(Calendar.MINUTE);

                // Chuyển về phút để so sánh
                int nowTotalMin = nowHour * 60 + nowMinute;
                int openTotalMin = openHour * 60 + openMinute;
                int endTotalMin = endHour * 60 + endMinute;

                if (nowTotalMin >= openTotalMin && nowTotalMin < endTotalMin) {
                    binding.tvShopStatus.setText("Opening");
                    binding.tvShopStatus.setTextColor(Color.GREEN);
                } else {
                    binding.tvShopStatus.setText("Closing");
                    binding.tvShopStatus.setTextColor(Color.RED);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing time", e);
                binding.tvShopStatus.setText("Unknown");
                binding.tvShopStatus.setTextColor(Color.GRAY);
            }


            // Set Category List
            List<CategoriesInShopMenu> categories = Optional.ofNullable(shopDetailResponse.getCategories())
                    .orElse(new ArrayList<>());
            // Thêm Category đặc biệt "ALL" vào đầu danh sách
            CategoriesInShopMenu allCategory = new CategoriesInShopMenu("ALL", "All");
            categories.add(0, allCategory);

            categoryAdapter = new CategoryAdapter(categories, categoryId -> {
                List<MenuItemResponse> allItems = Optional.ofNullable(mShopViewModel.getShopDetailResponse().getValue().getMenuItems())
                        .orElse(new ArrayList<>());
                if ("ALL".equals(categoryId)) {
                    // Nếu chọn "All", hiển thị toàn bộ món ăn
                    menuItemAdapter.updateData(allItems);
                    binding.tvCategoryTitle.setText("All (" + allItems.size() + ")"); // All (10)
                } else {
                    // Lọc theo category
                    List<MenuItemResponse> filteredItems = new ArrayList<>();
                    for (MenuItemResponse item : allItems) {
                        if (categoryId.equals(item.getCategoryId())) {
                            filteredItems.add(item);
                        }
                    }
                    menuItemAdapter.updateData(filteredItems);
                    String categoryName = categories.stream()
                            .filter(category -> category.getId().equals(categoryId))
                            .findFirst()
                            .map(CategoriesInShopMenu::getName)
                            .orElse("Unknown Category");
                    binding.tvCategoryTitle.setText(categoryName + " (" + filteredItems.size() + ")");
                }

            });
            recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerCategory.setAdapter(categoryAdapter);
            // Set Menu Items
            List<MenuItemResponse> menuItems = Optional.ofNullable(shopDetailResponse.getMenuItems())
                    .orElse(new ArrayList<>());
            Log.d(TAG, "setUpObservers: Menu Items: " + menuItems.size());
            menuItemAdapter = new MenuItemAdapter(menuItems, mAuthViewModel.getUserRole().getValue() != null ? mAuthViewModel.getUserRole().getValue() : "Guest", new MenuItemAdapter.OnMenuItemActionListener() {
                @Override
                public void onUpdate(MenuItemResponse item) {
                    mShopViewModel.setSelectedMenuItem(item);
                    navController.navigate(R.id.action_shopDetailFragment_to_addProductFragment);
                }

                @Override
                public void onDelete(MenuItemResponse item) {
                    mShopViewModel.deleteMenuItem(item.getId());
                }

                @Override
                public void onClick(MenuItemResponse item) {
                    if ("ShopOwner".equalsIgnoreCase(mAuthViewModel.getUserRole().getValue())) {
                        // Nếu là ShopOwner, không làm gì
                        return;
                    }
                    handleSelectItem(item);
                }
            });
            recyclerMenuItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerMenuItem.setAdapter(menuItemAdapter);
            binding.tvCategoryTitle.setText("All (" + menuItems.size() + ")"); // All (10)

        });


        mShopViewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void handleSelectItem(MenuItemResponse item) {
        // Hiển thị thông tin sản phẩm đã chọn
        binding.layoutSelectedProductInfo.setVisibility(View.VISIBLE);
        binding.btnPlus.setVisibility(View.VISIBLE);
        binding.btnMinus.setVisibility(View.VISIBLE);
        binding.tvSelectedProductName.setText(item.getName());
        binding.tvSelectedProductPrice.setText(CurrencyUtils.formatToVND(item.getPrice()));
        binding.tvSelectedProductDesc.setText(item.getDescription());

        quantity = 1;
        binding.tvQuantity.setText(String.valueOf(quantity));
        updateTotalPrice(CurrencyUtils.parseVNDToRaw(binding.tvSelectedProductPrice.getText().toString()));

        // Lưu thông tin sản phẩm đã chọn vào ViewModel
//        mShopViewModel.setSelectedMenuItem(item);
    }

    private void updateTotalPrice(String price) {
        try {
            int total = Integer.parseInt(price) * quantity;
            binding.tvTotalPrice.setText(CurrencyUtils.formatToVND(total));
        } catch (NumberFormatException e) {
            binding.tvTotalPrice.setText("0 VND");
        }

    }

}