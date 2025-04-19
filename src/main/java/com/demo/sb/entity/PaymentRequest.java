package com.demo.sb.entity;

public class PaymentRequest {
    private float amount;
    private int payerId;
    private int receiverId;
    private int courseId;
    private int cardId;

    // Getters and setters
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    public int getPayerId() { return payerId; }
    public void setPayerId(int payerId) { this.payerId = payerId; }
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }
}