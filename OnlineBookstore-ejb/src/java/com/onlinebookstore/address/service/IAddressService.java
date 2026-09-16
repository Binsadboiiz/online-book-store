package com.onlinebookstore.address.service;

import com.onlinebookstore.address.dto.AddressResponse;
import com.onlinebookstore.address.dto.CreateAddressRequest;
import com.onlinebookstore.address.dto.UpdateAddressRequest;
import com.onlinebookstore.common.dto.ApiResponse;
import java.util.List;

public interface IAddressService {

    ApiResponse<AddressResponse> createAddress(Integer userId, CreateAddressRequest request);

    ApiResponse<List<AddressResponse>> getAddressByUser(Integer userId);

    ApiResponse<AddressResponse> getAddressById(Integer userId, Integer addressId);

    ApiResponse<AddressResponse> getAddressByStatus(Integer userId, boolean isDefault);

    ApiResponse<AddressResponse> updateAddress(Integer userId, Integer addressId, UpdateAddressRequest request);

    ApiResponse<String> deleteAddress(Integer addressId);

    ApiResponse<String> deleteAddress(Integer userId, Integer addressId);

    ApiResponse<AddressResponse> setDefaultAddress(Integer userId, Integer addressId);
}
