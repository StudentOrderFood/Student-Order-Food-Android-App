package prm392.orderfood.androidapp.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.ItemMenuBinding;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    private List<MenuItemResponse> items;

    public MenuItemAdapter(List<MenuItemResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMenuBinding binding = ItemMenuBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItemResponse item = items.get(position);
        if (item == null) {
            return;
        }

        holder.binding.tvName.setText(item.getName());
        holder.binding.tvDescription.setText(item.getDescription());
        holder.binding.tvPrice.setText(item.getPrice() + " VND");
        holder.binding.tvDescription.setText(item.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.highland_americano)
                .into(holder.binding.imgItem);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMenuBinding binding;

        public ViewHolder(@NonNull ItemMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
