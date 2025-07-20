package prm392.orderfood.androidapp.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import prm392.orderfood.androidapp.databinding.ItemOrderBinding;
import prm392.orderfood.androidapp.utils.CurrencyUtils;
import prm392.orderfood.domain.models.menuItem.MenuItemResponse;
import prm392.orderfood.domain.models.orders.OrderRealTime;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    private final List<OrderRealTime> orders;
    private final List<MenuItemResponse> menuItems; // danh sách món từ ShopViewModel
    private final Set<String> highlightedOrders = new HashSet<>();

    public OrderAdapter(List<OrderRealTime> orders, List<MenuItemResponse> menuItems) {
        this.orders = orders;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemOrderBinding binding = ItemOrderBinding.inflate(inflater, parent, false);
        return new OrderViewHolder(binding);    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderRealTime order = orders.get(position);
        holder.binding.tvCustomerId.setText("Khách: " + order.getCustomerId());
        holder.binding.tvPaymentMethod.setText("Thanh toán: " + order.getPaymentMethod());
        holder.binding.tvTotalAmount.setText("Tổng tiền: " + CurrencyUtils.formatToVND(order.getTotalAmount()));

        // Nested RecyclerView
        OrderItemAdapter itemAdapter = new OrderItemAdapter(order.getOrderItems(), menuItems);
        holder.binding.rvOrderItems.setAdapter(itemAdapter);

        // Highlight
        if (highlightedOrders.contains(order.getFirebaseId())) {
            holder.binding.getRoot().setBackgroundColor(0xFFE0F7FA); // Màu xanh nhạt

            holder.binding.getRoot().postDelayed(() -> {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    String currentId = orders.get(currentPos).getFirebaseId();
                    highlightedOrders.remove(currentId);
                    notifyItemChanged(currentPos); // Gọi lại chính item đó để vẽ lại màu
                }
            }, 3000);
        } else {
            holder.binding.getRoot().setBackgroundColor(0xFFFFFFFF);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<OrderRealTime> newOrders) {
        for (OrderRealTime newOrder : newOrders) {
            String id = newOrder.getFirebaseId();
            int existingIndex = findIndexById(id);

            if (existingIndex == -1) {
                // New order -> insert on top
                orders.add(0, newOrder);
                highlightedOrders.add(id);
                notifyItemInserted(0);
            } else if (!newOrder.equals(orders.get(existingIndex))) {
                // Updated order
                orders.set(existingIndex, newOrder);
                highlightedOrders.add(id);
                notifyItemChanged(existingIndex);
            }
        }
    }

    private int findIndexById(String firebaseId) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getFirebaseId().equals(firebaseId)) {
                return i;
            }
        }
        return -1;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final ItemOrderBinding binding;

        public OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
