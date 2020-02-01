package com.thebiglosers.phix.model;

import java.util.HashMap;
import java.util.Map;

public class HomeDataModel {

    int todaysExpense;

    int monthsExpense;

    Map<Integer, Integer> data = new HashMap<Integer, Integer>();

    public HomeDataModel(int todaysExpense, int monthsExpense, Map<Integer, Integer> data) {
        this.todaysExpense = todaysExpense;
        this.monthsExpense = monthsExpense;
        this.data = data;
    }

    public int getTodaysExpense() {
        return todaysExpense;
    }

    public int getMonthsExpense() {
        return monthsExpense;
    }

    public Map<Integer,Integer> getData() {
        return data;
    }


}
