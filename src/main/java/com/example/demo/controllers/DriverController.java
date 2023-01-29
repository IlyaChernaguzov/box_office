package com.example.demo.controllers;

import com.example.demo.model.dto.DriverDTO;
import com.example.demo.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    private ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO){
        return ResponseEntity.ok(driverService.create(driverDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    private ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverDTO driverDTO){
        return ResponseEntity.ok(driverService.update(driverDTO));

    }

    @GetMapping
    private ResponseEntity<DriverDTO> getDriver(@RequestParam String email){
        return ResponseEntity.ok(driverService.get(email));

    }

    @DeleteMapping
    private ResponseEntity<HttpStatus> deleteDriver(@RequestParam String email){
        driverService.delete(email);
        return ResponseEntity.ok().build();

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
