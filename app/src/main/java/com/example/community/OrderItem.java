package com.example.community;

public class OrderItem {
    private String orderID;
    private String foodID;
    private String foodName;
    private String foodPic;
    private String foodDesc;
    private double foodPrice;
    private String merchantName;
    private String merchantAddress;
    private String customerName;
    private String customerPhone;
    private String orderDate;
    private String orderTime;
    private String orderStatus;
    private int quantity;

    // No-argument constructor for Firebase
    public OrderItem() {}

    // Full constructor
    public OrderItem(String orderID, String foodID, String foodName, String foodPic, String foodDesc,
                     double foodPrice, String merchantName, String merchantAddress,
                     String customerName, String customerPhone, String orderDate,
                     String orderTime, String orderStatus, int quantity) {
        this.orderID = orderID;
        this.foodID = foodID;
        this.foodName = foodName;
        this.foodPic = foodPic;
        this.foodDesc = foodDesc;
        this.foodPrice = foodPrice;
        this.merchantName = merchantName;
        this.merchantAddress = merchantAddress;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
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

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPic() {
        return foodPic;
    }

    public void setFoodPic(String foodPic) {
        this.foodPic = foodPic;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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