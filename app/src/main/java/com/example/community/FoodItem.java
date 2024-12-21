package com.example.community;

public class FoodItem {
    private String foodName;
    private String pickupShopName;
    private String foodPrice;
    private String foodDescription;
    private String imageUrl;

    public FoodItem() { }

    public FoodItem(String foodName, String pickupShopName, String foodPrice, String foodDescription, String imageUrl) {
        this.foodName = foodName;
        this.pickupShopName = pickupShopName;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
        this.imageUrl = imageUrl;
    }

    public String getFoodName() { return foodName; }
    public String getPickupShopName() { return pickupShopName; }
    public String getFoodPrice() { return foodPrice; }
    public String getFoodDescription() { return foodDescription; }
    public String getImageUrl() { return imageUrl; }
}
