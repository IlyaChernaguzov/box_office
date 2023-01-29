package com.example.demo.controllers;

import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.CarDTOResponse;
import com.example.demo.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    public final CarService carService;

    @PostMapping
    private ResponseEntity<CarDTORequest> createCar(@RequestBody CarDTORequest carDTORequest){
        return ResponseEntity.ok(carService.create(carDTORequest));// обертка ответа со статусом ок

    }

    @PutMapping
    private ResponseEntity<CarDTORequest> updateCar(@RequestBody CarDTORequest carDTORequest){
        return ResponseEntity.ok(carService.update(carDTORequest));

    }

    @GetMapping
    private ResponseEntity<CarDTORequest> getCar(@RequestParam String stateNumber){
        return ResponseEntity.ok(carService.get(stateNumber));

    }

    @DeleteMapping
    private ResponseEntity<HttpStatus> deleteCar(@RequestParam String stateNumber){
        carService.delete(stateNumber);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/carToDriver")
    private ResponseEntity<CarDTOResponse> addToDriver(@RequestBody String stateNumber, @RequestParam String email){
        return ResponseEntity.ok(carService.addToDriver(stateNumber, email));// обертка ответа со статусом ок

    }

}
