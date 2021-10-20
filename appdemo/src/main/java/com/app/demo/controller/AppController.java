package com.app.demo.controller;

import com.app.demo.model.CounterEntity;
import com.app.demo.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/counters")
public class AppController {

    private final AppService service;

    @Autowired
    public AppController(AppService service) {
        this.service = service;
    }

    @GetMapping("/get/{name}")
    public ResponseEntity<Object> getCounterByName(@PathVariable(value = "name") String counterName) {
        return service.getCounterCurrentValue(counterName);
    }

    @GetMapping("/getall")
    public ResponseEntity<Object> getListOfAllCounters() {
        return service.getAllCountersWithValues();
    }

    @PostMapping("/create")
    public ResponseEntity<Object> newCounter(@RequestBody CounterEntity entity) throws IOException {
        return service.createNewCounter(entity);
    }

    @PutMapping("/update/{name}")
    public ResponseEntity<Object> incrementCounter(@PathVariable(value = "name") String counterName) {
        return service.incrementCounter(counterName);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCounter(@PathVariable(value = "id") Long id) {
        return service.deleteCounterById(id);
    }
}
