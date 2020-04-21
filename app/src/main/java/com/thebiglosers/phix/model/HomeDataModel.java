package com.thebiglosers.phix.model;


import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;


public class HomeDataModel {

    Map<Integer, Integer> data = new HashMap<Integer, Integer>();

    @SerializedName("today")
    Double today;

    @SerializedName("month")
    Double month;

    @SerializedName("week")
    Double week;

    @SerializedName("daily_avg_current")
    Double dailyAvg;

    public HomeDataModel(Map<Integer, Integer> data, Double today, Double month, Double dailyAvg) {
        this.data = data;
        this.today = today;
        this.month = month;
        this.dailyAvg = dailyAvg;
    }

    public Map<Integer, Integer> getData() {
        return data;
    }

    public Double getToday() {
        return today;
    }

    public Double getMonth() {
        return month;
    }

    public Double getWeek() {
        return week;
    }

    public Double getDailyAvg() {
        return dailyAvg;
    }

}
