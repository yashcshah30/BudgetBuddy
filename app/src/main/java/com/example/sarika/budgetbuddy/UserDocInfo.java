package com.example.sarika.budgetbuddy;

public final class UserDocInfo {

    public final String categoryName;
    public  final int budget;
    public final int expense;

    public  UserDocInfo(String categoryName, int budget, int expense){
        this.categoryName=categoryName;
        this.budget=budget;
        this.expense=expense;
    }

    public  UserDocInfo(String categoryName, long budget, long expense){
        this.categoryName=categoryName;
        this.budget=(int)budget;
        this.expense=(int)expense;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public int getBudget(){
        return budget;
    }

    public int getExpense(){
        return expense;
    }

    public String toString(){
        return "Budget="+budget+",Expense="+expense;
    }
}
