package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.CarDTOResponse;
import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.entity.Car;
import com.example.demo.model.entity.Driver;
import com.example.demo.model.enums.CarStatus;
import com.example.demo.model.repository.CarRepository;
import com.example.demo.service.CarService;
import com.example.demo.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiseimpl implements CarService {

    private final DriverService driverService;//добавили сервис Водителя

    private final CarRepository carRepository;// связь с бд

    private final ObjectMapper mapper;// переопределяет сущности (carDTO в car)

    @Override
    public CarDTORequest create(CarDTORequest carDTORequest) {
        carRepository.findByStateNumber(carDTORequest.getStateNumber()).ifPresent(
                c -> {throw new CustomException("Транспорт с рег.номером" + carDTORequest.getStateNumber() + "уже существует", HttpStatus.BAD_REQUEST); // создаем исключение "если ТС уже существует, при создании выскачит ошибка"
                }
        );

        Car car = mapper.convertValue(carDTORequest, Car.class);
        car.setCreatedAt(LocalDateTime.now());
        Car save = carRepository.save(car);

        return mapper.convertValue(save, CarDTORequest.class);
    }

    @Override
    public CarDTORequest update(CarDTORequest carDTORequest) {
        Car car = getCar(carDTORequest.getStateNumber()); // создаем исключение "если ТС не найдено, при обновлении выскачит ошибка"

        car.setColorsCar(carDTORequest.getColorsCar() == null ? car.getColorsCar() : carDTORequest.getColorsCar()); // проверяем пришедшие данный на null с помощью "?". ":" - если не null, то присвиваем пришедшее значение
        car.setCarDate(carDTORequest.getCarDate() == null ? car.getCarDate() : carDTORequest.getCarDate());
        car.setBrandCar(carDTORequest.getBrandCar() == null ? car.getBrandCar() : carDTORequest.getBrandCar());
        car.setModelCar(carDTORequest.getModelCar() == null ? car.getModelCar() : carDTORequest.getModelCar());
        car.setStateNumber(carDTORequest.getStateNumber() == null ? car.getStateNumber() : carDTORequest.getStateNumber());
        car.setUpdatedAt(LocalDateTime.now());
        car.setStatus(CarStatus.UPDATED);
        Car save = carRepository.save(car);

        return mapper.convertValue(save, CarDTORequest.class);
    }

    @Override
    public CarDTORequest get(String stateNumber) {
        Car car = getCar(stateNumber);

        return mapper.convertValue(car, CarDTORequest.class);
    }

    @Override
    public void delete(String stateNumber) {
        Car car = getCar(stateNumber);

        car.setStatus(CarStatus.DELETED);// установить статут DELETED
        car.setUpdatedAt(LocalDateTime.now());
//        carRepository.delete(car);// полное удаление
        carRepository.save(car);
    }

    private Car getCar(String stateNumber){
        return carRepository.findByStateNumber(stateNumber)
                .orElseThrow(()-> new CustomException("Транспорт с рег.номером" + stateNumber + "не найден", HttpStatus.NOT_FOUND));// создаем исключение "если ТС не найдено, при обновлении выскачит ошибка"

    }

    @Override
    public CarDTOResponse addToDriver(String stateNumber, String email) {
        Driver driver = driverService.getDriver(email);
        Car car = getCar(stateNumber);
        car.setDriver(driver);
        Car save = carRepository.save(car);
        CarDTOResponse response = mapper.convertValue(save, CarDTOResponse.class);
        response.setDriverDTO(mapper.convertValue(driver, DriverDTO.class));
        return response;
    }

}
