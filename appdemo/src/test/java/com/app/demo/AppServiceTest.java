package com.app.demo;

import com.app.demo.model.CounterEntity;
import com.app.demo.repository.AppRepository;
import com.app.demo.service.AppService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppServiceTest {

    private AppService service;

    @Mock
    private AppRepository repository;

    @Before
    public void setUp() throws Exception {
        CounterEntity entity1 = getServiceUrlObject(1L, "foo");
        CounterEntity entity2 = getServiceUrlObject(10L, "bar");

        List<CounterEntity> counters = new ArrayList<>();
        counters.add(entity1);
        counters.add(entity2);

        when(repository.findByName("foo")).thenReturn(Optional.of(entity1));
        when(repository.findAll()).thenReturn(counters);
        service = new AppService(repository);
    }

    @Test
    public void testCreateNewCounter() {
        CounterEntity counterAlreadyExists = getServiceUrlObject(1L, "foo");

        assertEquals("foo already exists!", service.createNewCounter(counterAlreadyExists).getBody().toString());
        assertEquals("406 NOT_ACCEPTABLE", service.createNewCounter(counterAlreadyExists).getStatusCode().toString());

        CounterEntity counterNewlyCreated = getServiceUrlObject(2L, "pepsi");

        assertEquals("CounterEntity(id=2, name=pepsi, value=0, createdAt=2021-09-15T22:00, updatedAt=2021-09-15T23:00)",
                service.createNewCounter(counterNewlyCreated).getBody().toString());
        assertEquals("201 CREATED", service.createNewCounter(counterNewlyCreated).getStatusCode().toString());

        CounterEntity counterNull = getServiceUrlObject(3L, null);

        assertEquals("Name cannot be empty", service.createNewCounter(counterNull).getBody().toString());
        assertEquals("400 BAD_REQUEST", service.createNewCounter(counterNull).getStatusCode().toString());
    }

    @Test
    public void testIncrementCounter() {
        assertEquals("CounterEntity(id=1, name=foo, value=1, createdAt=2021-09-15T22:00, updatedAt=2021-09-15T23:00)", service.incrementCounter("foo").getBody().toString());
        assertEquals("202 ACCEPTED", service.incrementCounter("foo").getStatusCode().toString());
        assertEquals("Cannot be found counter by pepsi", service.incrementCounter("pepsi").getBody().toString());
        assertEquals("404 NOT_FOUND", service.incrementCounter("pepsi").getStatusCode().toString());
        assertEquals("Name cannot be empty", service.incrementCounter("").getBody().toString());
        assertEquals("400 BAD_REQUEST", service.incrementCounter("").getStatusCode().toString());
    }

    @Test
    public void testGetCounterCurrentValue() {
        assertEquals("CounterEntity(id=1, name=foo, value=0, createdAt=2021-09-15T22:00, updatedAt=2021-09-15T23:00)", service.getCounterCurrentValue("foo").getBody().toString());
        assertEquals("200 OK", service.getCounterCurrentValue("foo").getStatusCode().toString());
        assertEquals("Cannot be found counter by pepsi", service.getCounterCurrentValue("pepsi").getBody().toString());
        assertEquals("404 NOT_FOUND", service.getCounterCurrentValue("pepsi").getStatusCode().toString());
        assertEquals("Name cannot be empty", service.getCounterCurrentValue("").getBody().toString());
        assertEquals("400 BAD_REQUEST", service.getCounterCurrentValue("").getStatusCode().toString());
    }

    @Test
    public void testGetAllCountersWithValues() {
        assertEquals("[CounterEntity(id=1, name=foo, value=0, createdAt=2021-09-15T22:00, updatedAt=2021-09-15T23:00), CounterEntity(id=10, name=bar, value=0, createdAt=2021-09-15T22:00, updatedAt=2021-09-15T23:00)]", service.getAllCountersWithValues().getBody().toString());
        assertEquals("200 OK", service.getAllCountersWithValues().getStatusCode().toString());
    }

    @Test
    public void testDeleteCounterById() {
        assertEquals("Cannot be found counter by 6", service.deleteCounterById(6l).getBody().toString());
        assertEquals("404 NOT_FOUND", service.deleteCounterById(6l).getStatusCode().toString());
    }

    private CounterEntity getServiceUrlObject(Long id, String name) {
        return new CounterEntity(id, name, 0,
                LocalDateTime.of(2021, 9, 15, 22, 00),
                LocalDateTime.of(2021, 9, 15, 23, 00));
    }
}
