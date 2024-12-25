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

        // Pass both the FoodItem and its key to the listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodItemClick(foodItem, foodKey);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, restaurantName; // Ensure restaurantName is included
        ImageView foodImage;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
            restaurantName = itemView.findViewById(R.id.restaurantName); // Initialize restaurantName

            Log.d("FoodViewHolder", "restaurantName: " + (restaurantName != null));
        }
    }
}

