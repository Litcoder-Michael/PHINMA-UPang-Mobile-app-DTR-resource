package com.example.capstoneprojectfinal;

public class Model {
    private String date;
    private String timein;
    private String timeout;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimein() {
        return timein;
    }

    public void setTimein(String timein) {
        this.timein = timein;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getTotalHrs() {
        return totalHrs;
    }

    public void setTotalHrs(String totalHrs) {
        this.totalHrs = totalHrs;
    }

    public Model() {
        this.date = date;
        this.timein = timein;
        this.timeout = timeout;
        this.totalHrs = totalHrs;
    }

    private String totalHrs;
}
