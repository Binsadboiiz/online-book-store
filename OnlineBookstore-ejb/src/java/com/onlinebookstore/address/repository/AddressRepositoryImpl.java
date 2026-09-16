package com.onlinebookstore.address.repository;

import com.onlinebookstore.address.entity.Addresses;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class AddressRepositoryImpl implements IAddressRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public List<Addresses> findByUserId(Integer userId) {
        try {
            return entityManager.createQuery("SELECT a FROM Addresses a WHERE a.userId.id = :userId", Addresses.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Addresses findById(Integer id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Addresses.class, id.longValue());
    }

    @Override
    public Addresses findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Addresses.class, id);
    }

    @Override
    public Optional<Addresses> findDefaultByUser(Integer userId) {
        try {
            List<Addresses> list = entityManager.createQuery("SELECT a FROM Addresses a WHERE a.userId.id = :userId AND a.isDefault = true", Addresses.class)
                    .setParameter("userId", userId)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Addresses save(Addresses address) {
        entityManager.persist(address);
        return address;
    }

    @Override
    public Addresses update(Addresses address) {
        return entityManager.merge(address);
    }

    @Override
    public boolean deleteAddress(Integer id) {
        if (id == null) {
            return false;
        }
        return deleteAddress(id.longValue());
    }

    @Override
    public boolean deleteAddress(Long id) {
        Addresses address = findById(id);
        if (address != null) {
            entityManager.remove(address);
            return true;
        }
        return false;
    }

    @Override
    public void resetDefaultAddressForUser(Integer userId) {
        try {
            entityManager.createQuery("UPDATE Addresses a SET a.isDefault = false WHERE a.userId.id = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();
        } catch (Exception e) {
            return;
        }
    }
}
