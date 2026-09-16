package com.onlinebookstore.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class CreateAddressRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Recipient name is required")
    @Size(min = 1, max = 100, message = "Recipient name must be between 1 and 100 characters")
    private String recipientName;

    @NotBlank(message = "Phone number is required")
    @Size(min = 1, max = 20, message = "Phone number must be between 1 and 20 characters")
    private String phone;

    @NotBlank(message = "Address line is required")
    @Size(min = 1, max = 255, message = "Address line must be between 1 and 255 characters")
    private String addressLine;

    @NotBlank(message = "Ward is required")
    @Size(min = 1, max = 100, message = "Ward must be between 1 and 100 characters")
    private String ward;

    @NotBlank(message = "District is required")
    @Size(min = 1, max = 100, message = "District must be between 1 and 100 characters")
    private String district;

    @NotBlank(message = "City is required")
    @Size(min = 1, max = 100, message = "City must be between 1 and 100 characters")
    private String city;

    private Boolean isDefault;

    public CreateAddressRequest() {
    }

    public CreateAddressRequest(String recipientName, String phone, String addressLine, String ward, String district, String city, Boolean isDefault) {
        this.recipientName = recipientName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
