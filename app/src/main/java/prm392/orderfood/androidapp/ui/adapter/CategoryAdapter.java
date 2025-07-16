package prm392.orderfood.androidapp.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import prm392.orderfood.androidapp.databinding.ItemCategoryBinding;
import prm392.orderfood.domain.models.category.CategoriesInShopMenu;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoriesInShopMenu> categoryList;
    private int selectedPosition = 0;

    public CategoryAdapter(List<CategoriesInShopMenu> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CategoriesInShopMenu item = categoryList.get(position);
        ItemCategoryBinding b = holder.binding;

        b.txtCategory.setText(item.getName());
//        b.imgIcon.setImageResource(item.getIconResId());

        if (position == selectedPosition) {
            b.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"));
            b.txtCategory.setTextColor(Color.WHITE);
            b.imgIcon.setColorFilter(Color.WHITE);
        } else {
            b.cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
            b.txtCategory.setTextColor(Color.parseColor("#666666"));
            b.imgIcon.setColorFilter(Color.parseColor("#666666"));
        }

        b.getRoot().setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            // TODO: callback xử lý click nếu cần
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public ViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
