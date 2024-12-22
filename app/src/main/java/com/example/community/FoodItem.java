package com.example.community;

public class FoodItem {
    private String foodName;
    private double foodPrice;
    private String foodDesc;
    private String foodPic;
    private String merchantName;

    public FoodItem() {}

    public FoodItem(String foodName, double foodPrice, String foodDesc, String foodPic, String merchantName) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDesc = foodDesc;
        this.foodPic = foodPic;
        this.merchantName = merchantName;
    }

    public String getFoodName() { return foodName; }
    public double getFoodPrice() { return foodPrice; }
    public String getFoodDesc() { return foodDesc; }
    public String getFoodPic() { return foodPic; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
}