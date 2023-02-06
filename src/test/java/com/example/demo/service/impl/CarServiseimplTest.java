package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.CarDTOResponse;
import com.example.demo.model.entity.Car;
import com.example.demo.model.enums.Colors;
import com.example.demo.model.repository.CarRepository;
import com.example.demo.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarServiseimplTest {

    @InjectMocks
    private CarServiseimpl carService;

    @Mock
    private DriverService driverService;

    @Mock
    private CarRepository carRepository;

    @Spy
    private ObjectMapper mapper;

//    CarDTORequest test;
//
//    @Before
//    public void prepareTestData() {
//        test = new CarDTORequest();
//        test.setBrandCar("BMW");
//        test.setCarOld("1990");
//        test.setColorsCar(Colors.BLACK);
//        test.setModelCar("X5");
//        test.setStateNumber("f777ff77");
//    }

    @Test
    public void create() {
        CarDTORequest test = new CarDTORequest();// создаем объект
        test.setBrandCar("BMW");
        test.setCarOld("1990");
        test.setColorsCar(Colors.BLACK);
        test.setModelCar("X5");
        test.setStateNumber("f777ff77");

        when(carRepository.save(any(Car.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        //when(carRepository.save(any(Car.class))).thenReturn(any(Car.class));// иммитация сохранения в базу. Т.к. репозиторий замокан.

        CarDTOResponse res = carService.create(test);//вызываем проверяемый метод
        assertEquals(test.getModelCar(), res.getModelCar());// проверяем полученное значение с соданным
        assertEquals(test.getColorsCar(), res.getColorsCar());
        assertEquals(test.getStateNumber(), res.getStateNumber());
    }

    @Test(expected = CustomException.class)
    public void create_exception() {
        CarDTORequest test = new CarDTORequest();
        test.setBrandCar("BMW");
        test.setCarOld("1990");
        test.setColorsCar(Colors.BLACK);
        test.setModelCar("X5");
        test.setStateNumber("f777ff77");

        when(carRepository.findByStateNumber(anyString())).thenThrow(CustomException.class);// anyString - любая строка

        carService.create(test);

    }

    @Test
    public void update() {

        Car test = new Car();
        test.setBrandCar("BMW");
        test.setCarOld("1990");
        test.setColorsCar(Colors.BLACK);
        test.setModelCar("X5");
        test.setStateNumber("f777ff77");


        when(carRepository.findByStateNumber(anyString())).thenReturn(Optional.of(test));

        when(carRepository.save(any(Car.class)))
                .thenAnswer(i -> i.getArguments()[0]);// иммитация сохранения в базу. Т.к. репозиторий замокан.

        CarDTORequest testForUpdate = new CarDTORequest();// создаем объект с новыми данными
        testForUpdate.setCarOld("1992");
        testForUpdate.setModelCar("X6");
        testForUpdate.setStateNumber(test.getStateNumber());

        CarDTOResponse res = carService.update(testForUpdate);//вызываем проверяемый метод

        assertNotNull(res);// проверяем, что он не ноль
        assertSame(res.getStateNumber(),test.getStateNumber());//сверяем гос.номер результата и заданные аргументы метода
        assertEquals(res.getModelCar(), testForUpdate.getModelCar());//проверяем, обновилось ли model
        assertEquals(res.getCarOld(), testForUpdate.getCarOld());//смотрим результат по CarOld
        assertEquals(res.getBrandCar(),test.getBrandCar());//так как в экземпляре для обновления мы не задавали это поле, оно должно остаться прежним, проверяем это.
    }

    @Test(expected = CustomException.class)
    public void update_exception() {
        CarDTORequest test = new CarDTORequest();
        test.setBrandCar("BMW");
        test.setCarOld("1990");
        test.setColorsCar(Colors.BLACK);
        test.setModelCar("X5");
        test.setStateNumber("f777ff77");

        when(carRepository.findByStateNumber(anyString())).thenThrow(CustomException.class);// anyString - любая строка

        carService.update(test);

    }

    @Test
    public void get() {

        Car test = new Car();
        test.setBrandCar("BMW");
        test.setCarOld("1990");
        test.setColorsCar(Colors.BLACK);
        test.setModelCar("X5");
        test.setStateNumber("f777ff77");

        when(carRepository.findByStateNumber(anyString())).thenReturn(Optional.of(test));// иммитирует бд, что при поиске по номеру вернет объект типа Optional

        CarDTOResponse res = carService.get(test.getStateNumber());

        assertNotNull(res);
        assertEquals(res.getModelCar(), test.getModelCar());

    }

    @Test
    public void delete() {
        Car test = new Car();
        test.setStateNumber("f777ff77");

        when(carRepository.findByStateNumber(anyString())).thenReturn(Optional.of(test));
        when(carRepository.save(any(Car.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        carService.delete("f777ff77");

    }

    @Test
    public void addToDriver() {
    }

    @Test
    public void getAllCars() {
    }
}