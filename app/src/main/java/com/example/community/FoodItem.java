package com.example.community;

public class FoodItem {
    private String foodName;
    private double foodPrice; // Changed to double
    private String foodDesc;
    private String foodPic;
    private String pickupLocation;
    private String merchantName;
    private String status;
    private int quantity;

    // No-argument constructor (required for Firebase)
    public FoodItem() {
    }

    // Constructor
    public FoodItem(String foodName, double foodPrice, String foodDesc, String foodPic,
                    String pickupLocation, String merchantName, String status, int quantity) {
        this.foodName = foodName;
        this.foodPrice = foodPrice; // Accept double
        this.foodDesc = foodDesc;
        this.foodPic = foodPic;
        this.pickupLocation = pickupLocation;
        this.merchantName = merchantName;
        this.status = status;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getFoodName() {
        return foodName;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public String getFoodPic() {
        return foodPic;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public void setFoodPic(String foodPic) {
        this.foodPic = foodPic;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}