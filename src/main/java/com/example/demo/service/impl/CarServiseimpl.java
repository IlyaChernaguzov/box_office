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
import com.example.demo.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiseimpl implements CarService {

    private final DriverService driverService;//добавили сервис Водителя, чтобы для ТС присвоить Водителя

    private final CarRepository carRepository;// связь с бд

    private final ObjectMapper mapper;// конвертирует сущности (carDTO в car и обратно)

    @Override
    public CarDTOResponse create(CarDTORequest carDTORequest) {
        carRepository.findByStateNumber(carDTORequest.getStateNumber()).ifPresent(
                c -> {throw new CustomException("Транспорт с рег.номером" + carDTORequest.getStateNumber() + " уже существует", HttpStatus.BAD_REQUEST); // создаем исключение "если ТС уже существует, при создании выскачит ошибка"
                }
        );

        Car car = mapper.convertValue(carDTORequest, Car.class);//конвертируем запрос в сущность
        car.setCreatedAt(LocalDateTime.now());// записываем время создания
        Car save = carRepository.save(car);// сохроняем в бд

        return mapper.convertValue(save, CarDTOResponse.class);// конвертируем и возвращаем в виде ответа
    }

    @Override
    public CarDTOResponse update(CarDTORequest carDTORequest) {
        Car car = getCar(carDTORequest.getStateNumber()); // создаем исключение "если ТС не найдено, при обновлении выскачит ошибка"

        car.setColorsCar(carDTORequest.getColorsCar() == null ? car.getColorsCar() : carDTORequest.getColorsCar()); // проверяем пришедшие данный на null с помощью "?", если null, то присваиваем прошлое значение. ":" - если не null, то присвиваем пришедшее значение
        car.setCarOld(carDTORequest.getCarOld() == null ? car.getCarOld() : carDTORequest.getCarOld());
        car.setBrandCar(carDTORequest.getBrandCar() == null ? car.getBrandCar() : carDTORequest.getBrandCar());
        car.setModelCar(carDTORequest.getModelCar() == null ? car.getModelCar() : carDTORequest.getModelCar());
        car.setStateNumber(carDTORequest.getStateNumber() == null ? car.getStateNumber() : carDTORequest.getStateNumber());
        car.setUpdatedAt(LocalDateTime.now());// записываем время обновления
        car.setStatus(CarStatus.UPDATED);// записываем статус для наглядности
        Car save = carRepository.save(car);// сохроняем в бд

        return mapper.convertValue(save, CarDTOResponse.class);// конвертируем и возвращаем в виде ответа
    }

    @Override
    public CarDTOResponse get(String stateNumber) {
        Car car = getCar(stateNumber);
        DriverDTO driver = mapper.convertValue(car.getDriver(), DriverDTO.class);// конвертируем driver из бд в DriverDTO
        CarDTOResponse result = mapper.convertValue(car, CarDTOResponse.class);// конвертируем car из бд в CarDTOResponse - ответ
        result.setDriverDTO(driver);// устанавливаем водителя к ТС в ответе

        return result;
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
                .orElseThrow(()-> new CustomException("Транспорт с рег.номером" + stateNumber + " не найден", HttpStatus.NOT_FOUND));// создаем исключение в отдельном методе getCar "если ТС не найдено, при обновлении выскачит ошибка"

    }

    @Override
    public CarDTOResponse addToDriver(String stateNumber, String email) {
        Driver driver = driverService.getDriver(email); // получаем Водителя
        Car car = getCar(stateNumber);// получаем ТС
        car.setDriver(driver);// присвоили водителя ТС
        Car save = carRepository.save(car);// сохранили в бд
        CarDTOResponse response = mapper.convertValue(save, CarDTOResponse.class); // конвентировали ТС в ответ JSON
        response.setDriverDTO(mapper.convertValue(driver, DriverDTO.class));// конвентировали Водителя в ответ JSON и добавили к JSON ТС
        return response;
    }

    @Override
    public ModelMap getAllCars(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);//используем метод getPageRequest класса PaginationUtils для проверки полученных параметров
        Page<Car> pageResult = carRepository.findAll(pageRequest);// получает отсортированную страницу в формате Page из бд, согласно прешедшим параметрам. Из параметров Page можем вернуть только номер страницы и количество элементов

        List<CarDTORequest> content = pageResult.getContent().stream()// получаем все значения
                .map(c -> mapper.convertValue(c, CarDTORequest.class))// конвентируем все значения в CarDTORequest
                .collect(Collectors.toList()); // собираем в список

        PagedListHolder<CarDTORequest> result = new PagedListHolder<>(content);//PagedListHolder может передать больше данных

        result.setPage(page);
        result.setPageSize(perPage);

        ModelMap map = new ModelMap();// предает в связке с оберткой PagedListHolder ключ значение информации, которая требуется логикой
        map.addAttribute("content", result.getPageList());
        map.addAttribute("pageNumber", page);
        map.addAttribute("pageSize", result.getPageSize());
        map.addAttribute("totalPage", result.getPageCount());

        return map;
    }

}
