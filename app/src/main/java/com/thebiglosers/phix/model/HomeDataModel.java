package com.thebiglosers.phix.model;

import java.util.HashMap;
import java.util.Map;

public class HomeDataModel {

    float todaysExpense;

    float monthsExpense;

    Map<Integer, Integer> data = new HashMap<Integer, Integer>();

    public HomeDataModel(float todaysExpense, float monthsExpense, Map<Integer, Integer> data) {
        this.todaysExpense = todaysExpense;
        this.monthsExpense = monthsExpense;
        this.data = data;
    }

    public float getTodaysExpense() {
        return todaysExpense;
    }

    public float getMonthsExpense() {
        return monthsExpense;
    }

    public Map<Integer,Integer> getData() {
        return data;
    }


}
