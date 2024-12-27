package com.example.community;

import com.bumptech.glide.Glide;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodList;
    private List<String> foodKeys; // Add a list of keys
    private Context context;
    private OnFoodItemClickListener listener;

    public interface OnFoodItemClickListener {
        void onFoodItemClick(FoodItem foodItem, String foodKey); // Ensure foodKey is part of the interface
    }

    public FoodAdapter(Context context, List<FoodItem> foodList, List<String> foodKeys, OnFoodItemClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.foodKeys = foodKeys; // Initialize keys
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item_layout, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodList.get(position);
        String foodKey = foodKeys.get(position); // Get the corresponding key

        holder.foodName.setText(foodItem.getFoodName());
        holder.foodPrice.setText(String.format("RM %.2f", foodItem.getFoodPrice()));
        holder.restaurantName.setText(foodItem.getMerchantName()); // Set merchant name
        Glide.with(context).load(foodItem.getFoodPic()).into(holder.foodImage);

        // Update quantity display with conditional formatting
        int quantity = foodItem.getQuantity();
        holder.foodQuantity.setText("Quantity Available: " + quantity);

        if (quantity <= 2) {
            holder.foodQuantity.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.foodQuantity.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.foodQuantity.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.foodQuantity.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        // Handle "Out of Stock" status
        if (quantity == 0) {
            foodItem.setStatus("Out of Stock");
            holder.outOfStockOverlay.setVisibility(View.VISIBLE); // Updated ID
            holder.outOfStockLabel.setVisibility(View.VISIBLE); // Updated ID
            holder.itemView.setClickable(false);
        } else {
            foodItem.setStatus("Available");
            holder.outOfStockOverlay.setVisibility(View.GONE); // Updated ID
            holder.outOfStockLabel.setVisibility(View.GONE); // Updated ID
            holder.itemView.setClickable(true);
        }

        // Handle click for available items
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && quantity > 0) {
                listener.onFoodItemClick(foodItem, foodKeys.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, restaurantName, foodQuantity; // Ensure restaurantName is included
        ImageView foodImage;
        View outOfStockOverlay; // Updated ID
        TextView outOfStockLabel; // Updated ID

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
            restaurantName = itemView.findViewById(R.id.restaurantName); // Initialize restaurantName
            foodQuantity=itemView.findViewById(R.id.foodQuantity);
            outOfStockOverlay = itemView.findViewById(R.id.outOfStockOverlay); // Updated ID
            outOfStockLabel = itemView.findViewById(R.id.outOfStockLabel); // Updated ID

            Log.d("FoodViewHolder", "restaurantName: " + (restaurantName != null));
        }
    }
}

