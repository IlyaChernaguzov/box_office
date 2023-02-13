package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.CarDTOResponse;
import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.entity.Car;
import com.example.demo.model.entity.Driver;
import com.example.demo.model.enums.Colors;
import com.example.demo.model.enums.Gender;
import com.example.demo.model.repository.CarRepository;
import com.example.demo.model.repository.DriverRepository;
import com.example.demo.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.demo.model.enums.Gender.MALE;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CarServiseimplTest {

    @InjectMocks
    private CarServiseimpl carService;

    @Mock
    private DriverServiseimpl driverService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private DriverRepository driverRepository;

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

        verify(carRepository, times(1)).save(test);// произошло ли одно взаимодействие с БД

    }

    @Test
    public void addToDriver() {

//        DriverService driverService = mock(DriverService.class);

        Driver driver = new Driver();
        driver.setName("Ivan");
        driver.setEmail("test@mail.ru");
       // when(driverRepository.findByEmail(anyString())).thenReturn(Optional.of(driver));
        when(driverService.getDriver("test@mail.ru")).thenReturn(driver); //- проверяем DriverService, если не сделаны тесты DriverService

        Car car = new Car();
        car.setBrandCar("BMW");
        car.setCarOld("1990");
        car.setColorsCar(Colors.BLACK);
        car.setModelCar("X5");
        car.setStateNumber("f777ff77");
        when(carRepository.findByStateNumber(anyString())).thenReturn(Optional.of(car));

        when(carRepository.save(any(Car.class))).thenAnswer(i -> i.getArguments()[0]);

        DriverDTO driverTest = new DriverDTO();
        driverTest.setName("Ivan");
        driverTest.setSurname("Ivanov");
        driverTest.setGender(MALE);
        driverTest.setEmail(driver.getEmail());

        CarDTOResponse result = carService.addToDriver(car.getStateNumber(), driverTest.getEmail());
        assertEquals(car.getStateNumber(), result.getStateNumber());
        assertEquals(driver.getEmail(), result.getDriverDTO().getEmail());

    }

    @Test(expected = CustomException.class)
    public void addToDriver_exception() {
        CarDTORequest car = new CarDTORequest();
        car.setBrandCar("BMW");
        car.setCarOld("1990");
        car.setColorsCar(Colors.BLACK);
        car.setModelCar("X5");
        car.setStateNumber("f777ff77");

        when(carRepository.findByStateNumber(anyString())).thenThrow(CustomException.class);// anyString - любая строка

        carService.addToDriver(car.getStateNumber(), "test@mail.ru");
    }

//    @Test(expected = CustomException.class)
//    public void createDriver_exception() {
//        DriverDTO test = new DriverDTO();
//        test.setName("Ivan");
//        test.setSurname("Ivanov");
//        test.setGender(Gender.MALE);
//        test.setEmail("test@mail.ru");
//
//        when(driverRepository.findByEmail(anyString())).thenThrow(CustomException.class);
//
//        driverService.create(test);
//
//    }

    @Test
    public void getAllCars() {
        Integer page = 1;
        Integer perPage = 10;
        String sort = "BrandCar";
        Sort.Direction order = Sort.Direction.DESC; //задаем значения пагинации

        Car car = new Car();// создаем объект в бд
        car.setBrandCar("BMW"); //присваиваем значение

        List<Car> cars = Collections.singletonList(car);// добовляем в коллекцию

        Page<Car> pageResult = mock(Page.class); //мокаем Page

        when(carRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(cars);

        ModelMap result = carService.getAllCars(page, perPage, sort, order);
        List<CarDTORequest> carDTORequests = (List<CarDTORequest>) (result.get(
                "content"));
        assertEquals(car.getBrandCar(), carDTORequests.get(0).getBrandCar());

    }
}