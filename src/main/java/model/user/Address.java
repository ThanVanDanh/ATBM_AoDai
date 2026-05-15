package model.user;

import org.jdbi.v3.core.mapper.reflect.ColumnName;
import java.io.Serializable;

public class Address implements Serializable {
    private int id;
    @ColumnName("user_id")
    private int userId;
    @ColumnName("recipient_name")
    private String recipientName;
    @ColumnName("recipient_phone")
    private String recipientPhone;
    @ColumnName("address_line")
    private String addressLine;
    @ColumnName("city_province")
    private String cityProvince;
    private String country;
    @ColumnName("is_default")
    private boolean isDefault;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getCityProvince() { return cityProvince; }
    public void setCityProvince(String cityProvince) { this.cityProvince = cityProvince; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
    public boolean getIsDefault() {
        return isDefault;
    }
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
