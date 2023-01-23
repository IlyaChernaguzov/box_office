package com.example.demo.controllers;

import com.example.demo.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public String getDriverName(){

        return driverService.driverName();
    }

    @GetMapping("/surname")
    public String getDriverSurname(){
        return "Ivanov";
    }

    @GetMapping("/age")
    public String getDriverAge(){
        return "35";
    }

}
