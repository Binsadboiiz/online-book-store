package com.onlinebookstore.address.repository;

import com.onlinebookstore.address.entity.Addresses;
import java.util.List;
import java.util.Optional;

public interface IAddressRepository {
    List<Addresses> findByUserId(Integer userId);

    Addresses findById(Integer id);

    Addresses findById(Long id);

    Optional<Addresses> findDefaultByUser(Integer userId);

    Addresses save(Addresses address);

    Addresses update(Addresses address);

    boolean deleteAddress(Integer id);

    boolean deleteAddress(Long id);

    void resetDefaultAddressForUser(Integer userId);
}
