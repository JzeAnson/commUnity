package com.example.community;

public class FoodItem {
    private String foodName;
    private double foodPrice;
    private String foodDesc;
    private String foodPic;
    private String merchantName;
    private String merchantAddress; // New field
    private String status; // New field

    public FoodItem() {}

    public FoodItem(String foodName, double foodPrice, String foodDesc, String foodPic, String merchantName, String merchantAddress, String status) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDesc = foodDesc;
        this.foodPic = foodPic;
        this.merchantName = merchantName;
        this.merchantAddress = merchantAddress;
        this.status = status;
    }

    public String getFoodName() { return foodName; }
    public double getFoodPrice() { return foodPrice; }
    public String getFoodDesc() { return foodDesc; }
    public String getFoodPic() { return foodPic; }
    public String getMerchantName() { return merchantName; }
    public String getMerchantAddress() { return merchantAddress; }
    public String getStatus() { return status; }

    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setFoodPrice(double foodPrice) { this.foodPrice = foodPrice; }
    public void setFoodDesc(String foodDesc) { this.foodDesc = foodDesc; }
    public void setFoodPic(String foodPic) { this.foodPic = foodPic; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public void setMerchantAddress(String merchantAddress) { this.merchantAddress = merchantAddress; }
    public void setStatus(String status) { this.status = status; }
}