package prm392.orderfood.androidapp.ui.fragment.shopOwner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.utils.FileUtils;
import prm392.orderfood.androidapp.viewModel.ShopViewModel;
import prm392.orderfood.domain.models.shops.Shop;

@AndroidEntryPoint
public class ShopFormFragment extends Fragment {

    private RecyclerView rvSubImages;
    private SubImageAdapter subImageAdapter;
    private EditText etName, etAddress, etOpenHours, etEndHours;
    private Button btnSubmit, btnSelectImage, btnSelectSubImages;
    private ImageView ivPreviewImage;
    private ProgressBar progressBar;

    private ShopViewModel shopViewModel;

    private File imageFile;
    private List<File> subImageFiles = new ArrayList<>();

    private String editingShopId = null;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    imageFile = FileUtils.getFileFromUri(requireContext(), uri);
                    ivPreviewImage.setImageURI(uri);
                    ivPreviewImage.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<Intent> subImagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    List<Uri> uris = new ArrayList<>();
                    if (result.getData().getClipData() != null) {
                        for (int i = 0; i < result.getData().getClipData().getItemCount(); i++) {
                            uris.add(result.getData().getClipData().getItemAt(i).getUri());
                        }
                    } else {
                        uris.add(result.getData().getData());
                    }

                    for (Uri uri : uris) {
                        File file = FileUtils.getFileFromUri(requireContext(), uri);
                        subImageFiles.add(file);
                    }

                    Toast.makeText(requireContext(), "Đã chọn " + subImageFiles.size() + " ảnh phụ", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        checkEditMode();
        setupObservers();
        setupListeners();
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etOpenHours = view.findViewById(R.id.etOpenHours);
        etEndHours = view.findViewById(R.id.etEndHours);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnSelectSubImages = view.findViewById(R.id.btnSelectSubImages);
        ivPreviewImage = view.findViewById(R.id.ivPreviewImage);
        progressBar = view.findViewById(R.id.progressBar);
        rvSubImages = view.findViewById(R.id.rvSubImages);
    }

    private void setupRecyclerView() {
        rvSubImages.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        subImageAdapter = new SubImageAdapter();
        rvSubImages.setAdapter(subImageAdapter);
    }

    private void setupViewModel() {
        shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);
    }

    private void checkEditMode() {
        editingShopId = null; // Reset before checking
        Bundle args = getArguments();
        if (args != null) {
            String id = args.getString("shopId");
            if (id != null && !id.trim().isEmpty()) {
                editingShopId = id;
                shopViewModel.getShopById(editingShopId);
            }
        }
    }

    private void setupObservers() {
        shopViewModel.shopDetail.observe(getViewLifecycleOwner(), shop -> {
            if (shop != null) {
                etName.setText(shop.getName());
                etAddress.setText(shop.getAddress());
                etOpenHours.setText(shop.getOpenHours());
                etEndHours.setText(shop.getEndHours());

                if (shop.getImageUrl() != null) {
                    Glide.with(this)
                            .load(shop.getImageUrl())
                            .into(ivPreviewImage);
                    ivPreviewImage.setVisibility(View.VISIBLE);
                }

                if (shop.getImages() != null && !shop.getImages().isEmpty()) {
                    rvSubImages.setVisibility(View.VISIBLE);
                    subImageAdapter.setImageUrls(shop.getImages());
                }
            }
        });

        shopViewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        shopViewModel.actionSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Thành công!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        shopViewModel.errorMessage.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSelectSubImages.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            subImagePickerLauncher.launch(intent);
        });

        btnSubmit.setOnClickListener(v -> {
            Shop shop = new Shop();
            shop.setName(etName.getText().toString());
            shop.setAddress(etAddress.getText().toString());
            shop.setOpenHours(etOpenHours.getText().toString());
            shop.setEndHours(etEndHours.getText().toString());

            if (editingShopId != null && !editingShopId.isEmpty()) {
                shop.setId(editingShopId);
                shopViewModel.updateShop(shop, imageFile, subImageFiles);
            } else {
                shopViewModel.createShop(shop, imageFile, subImageFiles);
            }
        });
    }
}

