package model.policy;

import java.sql.Timestamp;

public class Policy {
    private int id;
    private int categoryId;
    private String policyText;
    private Timestamp createdAt;

    public Policy() {
    }

    public Policy(int id) {
        this.id = id;
    }

    public Policy(int id, int categoryId, String policyText, Timestamp createdAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.policyText = policyText;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getPolicyText() {
        return policyText;
    }

    public void setPolicyText(String policyText) {
        this.policyText = policyText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", policyText='" + policyText + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
