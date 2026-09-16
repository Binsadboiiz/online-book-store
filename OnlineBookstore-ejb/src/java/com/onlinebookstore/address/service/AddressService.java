package com.onlinebookstore.address.service;

import com.onlinebookstore.address.dto.AddressResponse;
import com.onlinebookstore.address.dto.CreateAddressRequest;
import com.onlinebookstore.address.dto.UpdateAddressRequest;
import com.onlinebookstore.address.entity.Addresses;
import com.onlinebookstore.address.repository.IAddressRepository;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ForbiddenException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class AddressService implements IAddressService {

    @Inject
    private IAddressRepository addressRepository;

    @Inject
    private IUserRepository userRepository;

    @Override
    public ApiResponse<AddressResponse> createAddress(Integer userId, CreateAddressRequest request) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null) {
            throw new BadRequestException("Request data cannot be null");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        List<Addresses> existingAddresses = addressRepository.findByUserId(userId);
        boolean isFirstAddress = (existingAddresses == null || existingAddresses.isEmpty());
        boolean shouldBeDefault = isFirstAddress || Boolean.TRUE.equals(request.getIsDefault());

        if (shouldBeDefault && !isFirstAddress) {
            addressRepository.resetDefaultAddressForUser(userId);
        }

        Date now = new Date();
        Addresses address = new Addresses();
        address.setUserId(user);
        address.setRecipientName(request.getRecipientName() != null ? request.getRecipientName().trim() : "");
        address.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");
        address.setAddressLine(request.getAddressLine() != null ? request.getAddressLine().trim() : "");
        address.setWard(request.getWard() != null ? request.getWard().trim() : "");
        address.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : "");
        address.setCity(request.getCity() != null ? request.getCity().trim() : "");
        address.setIsDefault(shouldBeDefault);
        address.setCreatedAt(now);
        address.setUpdatedAt(now);

        Addresses savedAddress = addressRepository.save(address);
        return ApiResponse.success("Add address successfully", AddressResponse.fromEntity(savedAddress));
    }

    @Override
    public ApiResponse<List<AddressResponse>> getAddressByUser(Integer userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        List<Addresses> addresses = addressRepository.findByUserId(userId);
        if (addresses == null) {
            addresses = new ArrayList<>();
        }

        List<AddressResponse> responseList = addresses.stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Get addresses successfully", responseList);
    }

    @Override
    public ApiResponse<AddressResponse> getAddressById(Integer userId, Integer addressId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (addressId == null) {
            throw new BadRequestException("Invalid address ID");
        }

        Addresses address = addressRepository.findById(addressId);
        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        if (address.getUserId() == null || !address.getUserId().getId().equals(userId)) {
            throw new ForbiddenException("Access denied: You can only view your own address");
        }

        return ApiResponse.success("Get address successfully", AddressResponse.fromEntity(address));
    }

    @Override
    public ApiResponse<AddressResponse> getAddressByStatus(Integer userId, boolean isDefault) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (isDefault) {
            Optional<Addresses> defaultAddressOpt = addressRepository.findDefaultByUser(userId);
            if (defaultAddressOpt.isPresent()) {
                return ApiResponse.success("Get default address successfully", AddressResponse.fromEntity(defaultAddressOpt.get()));
            }
            throw new ResourceNotFoundException("Default address not found");
        } else {
            List<Addresses> addresses = addressRepository.findByUserId(userId);
            if (addresses != null && !addresses.isEmpty()) {
                for (Addresses addr : addresses) {
                    if (!addr.getIsDefault()) {
                        return ApiResponse.success("Get non-default address successfully", AddressResponse.fromEntity(addr));
                    }
                }
            }
            throw new ResourceNotFoundException("Non-default address not found");
        }
    }

    @Override
    public ApiResponse<AddressResponse> updateAddress(Integer userId, Integer addressId, UpdateAddressRequest request) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (addressId == null) {
            throw new BadRequestException("Invalid address ID");
        }

        if (request == null) {
            throw new BadRequestException("Request data cannot be null");
        }

        Addresses address = addressRepository.findById(addressId);
        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        if (address.getUserId() == null || !address.getUserId().getId().equals(userId)) {
            throw new ForbiddenException("Access denied: You can only update your own address");
        }

        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.getIsDefault()) {
            addressRepository.resetDefaultAddressForUser(userId);
            address.setIsDefault(true);
        } else if (Boolean.FALSE.equals(request.getIsDefault())) {
            address.setIsDefault(false);
        }

        address.setRecipientName(request.getRecipientName() != null ? request.getRecipientName().trim() : address.getRecipientName());
        address.setPhone(request.getPhone() != null ? request.getPhone().trim() : address.getPhone());
        address.setAddressLine(request.getAddressLine() != null ? request.getAddressLine().trim() : address.getAddressLine());
        address.setWard(request.getWard() != null ? request.getWard().trim() : address.getWard());
        address.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : address.getDistrict());
        address.setCity(request.getCity() != null ? request.getCity().trim() : address.getCity());
        address.setUpdatedAt(new Date());

        Addresses updatedAddress = addressRepository.update(address);
        return ApiResponse.success("Update address successfully", AddressResponse.fromEntity(updatedAddress));
    }

    @Override
    public ApiResponse<String> deleteAddress(Integer addressId) {
        if (addressId == null) {
            throw new BadRequestException("Invalid address ID");
        }

        Addresses address = addressRepository.findById(addressId);
        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        Integer userId = address.getUserId() != null ? address.getUserId().getId() : null;
        boolean wasDefault = address.getIsDefault();

        boolean deleted = addressRepository.deleteAddress(addressId);
        if (!deleted) {
            throw new BadRequestException("Failed to delete address");
        }

        if (wasDefault && userId != null) {
            List<Addresses> remaining = addressRepository.findByUserId(userId);
            if (remaining != null && !remaining.isEmpty()) {
                Addresses newDefault = remaining.get(0);
                newDefault.setIsDefault(true);
                addressRepository.update(newDefault);
            }
        }

        return ApiResponse.success("Delete address successfully", null);
    }

    @Override
    public ApiResponse<String> deleteAddress(Integer userId, Integer addressId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (addressId == null) {
            throw new BadRequestException("Invalid address ID");
        }

        Addresses address = addressRepository.findById(addressId);
        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        if (address.getUserId() == null || !address.getUserId().getId().equals(userId)) {
            throw new ForbiddenException("Access denied: You can only delete your own address");
        }

        return deleteAddress(addressId);
    }

    @Override
    public ApiResponse<AddressResponse> setDefaultAddress(Integer userId, Integer addressId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user ID");
        }

        if (addressId == null) {
            throw new BadRequestException("Invalid address ID");
        }

        Addresses address = addressRepository.findById(addressId);
        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        if (address.getUserId() == null || !address.getUserId().getId().equals(userId)) {
            throw new ForbiddenException("Access denied: You can only modify your own address");
        }

        addressRepository.resetDefaultAddressForUser(userId);
        address.setIsDefault(true);
        address.setUpdatedAt(new Date());

        Addresses updatedAddress = addressRepository.update(address);
        return ApiResponse.success("Set default address successfully", AddressResponse.fromEntity(updatedAddress));
    }
}
