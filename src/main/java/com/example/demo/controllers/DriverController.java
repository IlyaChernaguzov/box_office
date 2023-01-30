package com.example.demo.controllers;

import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.DriverDTO;
import com.example.demo.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Tag(name = "Водители")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @Operation(summary = "Создание водителя")// описание в svagger ui
    private ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO){
        return ResponseEntity.ok(driverService.create(driverDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных водителя")
    private ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverDTO driverDTO){
        return ResponseEntity.ok(driverService.update(driverDTO));

    }

    @GetMapping
    @Operation(summary = "Посмотреть водителя")
    private ResponseEntity<DriverDTO> getDriver(@RequestParam String email){
        return ResponseEntity.ok(driverService.get(email));

    }

    @DeleteMapping
    @Operation(summary = "Удалить водителя")
    private ResponseEntity<HttpStatus> deleteDriver(@RequestParam String email){
        driverService.delete(email);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать водителей")
    public List<DriverDTO> getAllDrivers(@RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                          @RequestParam(required = false, defaultValue = "surname") String sort,
                                          @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return driverService.getAllDrivers(page, perPage, sort, order);

    }







//    @PostMapping ("/surname")
//    public String getDriverSurname(){
//        return "Ivanov";
//    }
//
//    @PutMapping ("/age")
//    public String getDriverAge(){
//        return "35";
//    }

}
