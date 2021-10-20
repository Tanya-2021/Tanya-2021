package com.app.demo.service;

import com.app.demo.model.CounterEntity;
import com.app.demo.repository.AppRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {
    private static final String CANNOT_FOUND_COUNTER = "Cannot be found counter by ";
    private static final String NAME_CANNOT_BE_EMPTY = "Name cannot be empty";
    private static final String DELETED_SUCCESSFULLY = "\n is Deleted successfully";
    private static final String ALREADY_EXISTS = " already exists!";
    private final AppRepository appRepository;

    @Autowired
    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public ResponseEntity<Object> createNewCounter(CounterEntity counterEntity) {
        if (counterEntity.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(NAME_CANNOT_BE_EMPTY);
        }

        Optional<CounterEntity> counter = appRepository.findByName(counterEntity.getName());
        if (counter.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(counterEntity.getName() + ALREADY_EXISTS);
        }
        counterEntity.setValue(0);
        appRepository.save(counterEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(counterEntity);
    }

    public ResponseEntity<Object> incrementCounter(String counterName) {
        if (counterName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(NAME_CANNOT_BE_EMPTY);
        }

        Optional<CounterEntity> counter = appRepository.findByName(counterName);
        if (!counter.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CANNOT_FOUND_COUNTER + counterName);
        }

        appRepository.findByName(counterName)
                .map(c -> {
                    c.setValue(c.getValue() + 1);
                    return appRepository.save(c);
                })
                .orElseGet(() -> {
                    counter.get().setName(counterName);
                    return appRepository.save(counter.get());
                });

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(counter.get());
    }

    public ResponseEntity<Object> getCounterCurrentValue(String counterName) {
        if (Strings.isEmpty(counterName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(NAME_CANNOT_BE_EMPTY);
        }

        Optional<CounterEntity> counter = appRepository.findByName(counterName);
        if (!counter.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CANNOT_FOUND_COUNTER + counterName);
        }
        return ResponseEntity.status(HttpStatus.OK).body(counter.get());
    }

    public ResponseEntity<Object> getAllCountersWithValues() {
        List<CounterEntity> listOfCounters = new ArrayList<>();
        for (CounterEntity counter : appRepository.findAll()) listOfCounters.add(counter);
        return ResponseEntity.status(HttpStatus.OK).body(listOfCounters);
    }

    public ResponseEntity<Object> deleteCounterById(long id) {
        Optional<CounterEntity> counterToBeDeleted = appRepository.findById(id);
        if (!counterToBeDeleted.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CANNOT_FOUND_COUNTER + id);
        }

        appRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(counterToBeDeleted.get() + DELETED_SUCCESSFULLY);
    }
}