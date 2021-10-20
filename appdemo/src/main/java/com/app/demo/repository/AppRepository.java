package com.app.demo.repository;

import com.app.demo.model.CounterEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppRepository extends CrudRepository<CounterEntity, Long> {
    Optional<CounterEntity> findByName(String name);
}
