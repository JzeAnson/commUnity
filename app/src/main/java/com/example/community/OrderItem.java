package com.example.community;

public class OrderItem {
    private String orderID;
    private String foodName;
    private double foodPrice;
    private String foodPic;
    private String merchantName;
    private String orderDate;
    private String orderTime;
    private String orderStatus;
    private int quantity;

    // No-argument constructor for Firebase
    public OrderItem() {}

    // Full constructor
    public OrderItem(String orderID, String foodName, double foodPrice, String foodPic,
                     String merchantName, String orderDate, String orderTime,
                     String orderStatus, int quantity) {
        this.orderID = orderID;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodPic = foodPic;
        this.merchantName = merchantName;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
