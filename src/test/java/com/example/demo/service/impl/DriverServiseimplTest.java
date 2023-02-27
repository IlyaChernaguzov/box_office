package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.entity.Place;
import com.example.demo.model.enums.Genre;
import com.example.demo.model.repository.DriverRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DriverServiseimplTest {

    @InjectMocks
    private DriverServiseimpl driverService;

    @Mock
    private  DriverRepository driverRepository;

    @Spy
    private  ObjectMapper mapper;

    @Test
    public void create() {

        DriverDTO test = new DriverDTO();
        test.setName("Ivan");
        test.setSurname("Ivanov");
        test.setGender(Genre.MALE);
        test.setEmail("test@mail.ru");

        when(driverRepository.save(any(Place.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        DriverDTO res = driverService.create(test);
        assertEquals(test.getEmail(), res.getEmail());

    }

    @Test(expected = CustomException.class)
    public void create_exception() {
        DriverDTO test = new DriverDTO();
        test.setName("Ivan");
        test.setSurname("Ivanov");
        test.setGender(Genre.MALE);
        test.setEmail("test@mail.ru");

        when(driverRepository.findByEmail(anyString())).thenThrow(CustomException.class);

        driverService.create(test);

    }

    @Test
    public void update() {

        Place test = new Place();
        test.setName("Ivan");
        test.setSurname("Ivanov");
        test.setGender(Genre.MALE);
        test.setEmail("test@mail.ru");


        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.of(test));

        when(driverRepository.save(any(Place.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        DriverDTO testForUpdate = new DriverDTO();
        testForUpdate.setName("Alex");
        testForUpdate.setSurname("Petrov");
        testForUpdate.setGender(Genre.MALE);
        testForUpdate.setEmail(test.getEmail());

        DriverDTO res = driverService.update(testForUpdate);

        assertNotNull(res);
        assertSame(res.getName(),test.getName());
        assertEquals(res.getSurname(), testForUpdate.getSurname());
        assertEquals(res.getGender(), testForUpdate.getGender());

    }

    @Test(expected = CustomException.class)
    public void update_exception() {
        DriverDTO test = new DriverDTO();
        test.setName("Ivan");
        test.setSurname("Ivanov");
        test.setGender(Genre.MALE);
        test.setEmail("test@mail.ru");

        when(driverRepository.findByEmail(anyString())).thenThrow(CustomException.class);// anyString - любая строка

        driverService.update(test);

    }

    @Test
    public void get() {

        Place test = new Place();
        test.setName("Ivan");
        test.setSurname("Ivanov");
        test.setGender(Genre.MALE);
        test.setEmail("test@mail.ru");

        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.of(test));// иммитирует бд, что при поиске по номеру вернет объект типа Optional

        DriverDTO res = driverService.get(test.getEmail());

        assertNotNull(res);
        assertEquals(res.getName(), test.getName());
    }

    @Test
    public void delete() {

        Place test = new Place();
        test.setEmail("test@mail.ru");

        when(driverRepository.findByEmail(anyString())).thenReturn(Optional.of(test));
        when(driverRepository.save(any(Place.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        driverService.delete("test@mail.ru");

        verify(driverRepository, times(1)).save(test);
    }


    @Test
    public void getAllDrivers() {

        Integer page = 1;
        Integer perPage = 10;
        String sort = "BrandCar";
        Sort.Direction order = Sort.Direction.ASC;

        Place test = new Place();
        test.setEmail("test@mail.ru");

        List<Place> drivers = Collections.singletonList(test);

        Page<Place> pageResult = mock(Page.class);

        when(driverRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(drivers);

        List<DriverDTO> res = driverService.getAllDrivers(page, perPage, sort, order);

        assertEquals(res.get(0).getEmail(), test.getEmail());
    }
}