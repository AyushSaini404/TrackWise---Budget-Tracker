package com.example.trackwise_budgettracker;

public class CategoryAmount {
    private final String category;
    private final String amount;

    public CategoryAmount(String category, String amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public String getAmount() {
        return amount;
    }
}

