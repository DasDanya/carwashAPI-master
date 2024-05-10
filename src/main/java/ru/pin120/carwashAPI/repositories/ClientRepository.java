package ru.pin120.carwashAPI.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends PagingAndSortingRepository<Client, Long> {

    void save(Client client);

    Optional<Client> findByClId(Long clId);

    void deleteByClId(Long clId);
}
