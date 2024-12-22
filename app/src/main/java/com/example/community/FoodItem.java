package com.example.community;

public class FoodItem {
    private String foodName;
    private double foodPrice;
    private String foodDesc;
    private String foodPic;

    public FoodItem() {}

    public FoodItem(String foodName, double foodPrice, String foodDesc, String foodPic) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDesc = foodDesc;
        this.foodPic = foodPic;
    }

    public String getFoodName() { return foodName; }
    public double getFoodPrice() { return foodPrice; }
    public String getFoodDesc() { return foodDesc; }
    public String getFoodPic() { return foodPic; }
}