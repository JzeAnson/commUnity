package com.example.community;

public class FoodItem {
    private String foodName;
    private String foodPrice;
    private String foodDesc;
    private String foodPic;

    public FoodItem() {}

    public FoodItem(String foodName, String foodPrice, String foodDesc, String foodPic) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDesc = foodDesc;
        this.foodPic = foodPic;
    }

    public String getFoodName() { return foodName; }
    public String getFoodPrice() { return foodPrice; }
    public String getFoodDesc() { return foodDesc; }
    public String getFoodPic() { return foodPic; }
}