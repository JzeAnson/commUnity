package com.example.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<OrderItem> orderList;

    public OrderAdapter(Context context, List<OrderItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        holder.foodName.setText(order.getFoodName());
        holder.foodPrice.setText(String.format("RM %.2f", order.getFoodPrice()));
        holder.merchantName.setText(order.getMerchantName());
        holder.quantity.setText("Quantity: " + order.getQuantity());
        holder.orderStatus.setText(order.getOrderStatus());
        holder.orderDate.setText(order.getOrderDate());
        holder.orderTime.setText(order.getOrderTime());

        // Load food image
        Glide.with(context).load(order.getFoodPic()).into(holder.foodImage);

        // Set color for order status
        switch (order.getOrderStatus()) {
            case "Pending":
                holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "Completed":
                holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "Cancelled":
                holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, merchantName, quantity, orderStatus, orderDate, orderTime;
        ImageView foodImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            merchantName = itemView.findViewById(R.id.merchantName);
            quantity = itemView.findViewById(R.id.quantity);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTime = itemView.findViewById(R.id.orderTime);
            foodImage = itemView.findViewById(R.id.foodImage);
        }
    }
}
