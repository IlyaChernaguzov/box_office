package com.example.demo.controllers;

import com.example.demo.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public String getDriverName(){

        return driverService.driverName();
    }

    @PostMapping ("/surname")
    public String getDriverSurname(){
        return "Ivanov";
    }

    @PutMapping ("/age")
    public String getDriverAge(){
        return "35";
    }

}
