package com.example.demo.controllers;

import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.CarDTOResponse;
import com.example.demo.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@Tag(name = "Транспортные средства")
public class CarController {

    public final CarService carService;

    @PostMapping
    @Operation(summary = "Создание транспортного средства")// описание в svagger ui
    private ResponseEntity<CarDTORequest> createCar(@RequestBody CarDTORequest carDTORequest){
        return ResponseEntity.ok(carService.create(carDTORequest));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных транспортного средства")
    private ResponseEntity<CarDTOResponse> updateCar(@RequestBody CarDTOResponse carDTOResponse){
        return ResponseEntity.ok(carService.update(carDTOResponse));

    }

    @GetMapping
    @Operation(summary = "Посмотреть транспортное средство")
    private ResponseEntity<CarDTOResponse> getCar(@RequestParam String stateNumber){//возвращает именно ответ CarDTOResponse
        return ResponseEntity.ok(carService.get(stateNumber));

    }

    @DeleteMapping
    @Operation(summary = "Удаление транспортного средства")
    private ResponseEntity<HttpStatus> deleteCar(@RequestParam String stateNumber){
        carService.delete(stateNumber);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/carToDriver")
    @Operation(summary = "Присваивание транспортному средству водителя")
    private ResponseEntity<CarDTOResponse> addToDriver(@RequestBody String stateNumber, @RequestParam String email){
        return ResponseEntity.ok(carService.addToDriver(stateNumber, email));// обертка ответа со статусом ок

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать транспортные средства")
    public ModelMap getAllCars(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "10") Integer perPage,
                               @RequestParam(required = false, defaultValue = "carOld") String sort,
                               @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return carService.getAllCars(page, perPage, sort, order);

    }

}
