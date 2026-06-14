package model.order;

public class MonthlyRevenue {
    private String monthYear;
    private int orderCount;
    private double revenue;

    public MonthlyRevenue() {
    }

    public MonthlyRevenue(String monthYear, int orderCount, double revenue) {
        this.monthYear = monthYear;
        this.orderCount = orderCount;
        this.revenue = revenue;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
