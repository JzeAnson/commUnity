package com.example.community;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        // Bind data to UI components
        holder.foodName.setText(String.valueOf(order.getFoodName()));
        Log.d("OrderAdapter", "Food Name: " + order.getFoodName());
        holder.foodPrice.setText(String.format("RM %.2f", order.getFoodPrice()));
        holder.merchantName.setText(order.getMerchantName());
        holder.quantity.setText(String.valueOf(order.getQuantity()));
        holder.orderStatus.setText(order.getOrderStatus());
        holder.orderDate.setText(order.getOrderDate());
        holder.foodDesc.setText(order.getFoodDesc());

        // Load food image using Glide
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

        // Check user role and show/hide the Update Status button
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String currentRole = sharedPreferences.getString("userRole", "customer"); // Default role is customer
        if ("merchant".equals(currentRole)) {
            holder.updateStatusButton.setVisibility(View.VISIBLE);
            holder.updateStatusButton.setOnClickListener(v -> showUpdateStatusDialog(order));
        } else {
            holder.updateStatusButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void showUpdateStatusDialog(OrderItem order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Order Status")
                .setMessage("Did the customer pick up and pay for the food?")
                .setPositiveButton("Yes", (dialog, which) -> updateOrderStatus(order, "Completed"))
                .setNegativeButton("No", (dialog, which) -> updateOrderStatus(order, "Cancelled"))
                .setCancelable(false)
                .show();
    }

    private void updateOrderStatus(OrderItem order, String newStatus) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.child(order.getOrderID()).child("orderStatus").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Order status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                    order.setOrderStatus(newStatus); // Update local object
                    notifyDataSetChanged(); // Refresh the RecyclerView
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show();
                });
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, merchantName, quantity, orderStatus, orderDate, foodDesc;
        ImageView foodImage;
        Button updateStatusButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            merchantName = itemView.findViewById(R.id.merchantName);
            quantity = itemView.findViewById(R.id.quantity);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderDate = itemView.findViewById(R.id.orderDate);
            foodDesc = itemView.findViewById(R.id.foodDesc);
            foodImage = itemView.findViewById(R.id.foodImage);
            updateStatusButton = itemView.findViewById(R.id.update_status_button);
        }
    }
}