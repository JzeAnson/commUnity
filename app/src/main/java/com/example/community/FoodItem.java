package com.example.community;

public class FoodItem {
    private String foodName;
    private double foodPrice;
    private String foodDesc;
    private String foodPic;
    private String merchantName;
    private String merchantAddress;
    private String status;
    private int quantity;

    // No-argument constructor
    public FoodItem() {
    }

    // Constructor with parameters
    public FoodItem(String foodName, double foodPrice, String foodDesc, String foodPic,
                    String merchantName, String merchantAddress, String status, int quantity) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDesc = foodDesc;
        this.foodPic = foodPic;
        this.merchantName = merchantName;
        this.merchantAddress = merchantAddress;
        this.status = status;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public String getFoodPic() {
        return foodPic;
    }

    public void setFoodPic(String foodPic) {
        this.foodPic = foodPic;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}