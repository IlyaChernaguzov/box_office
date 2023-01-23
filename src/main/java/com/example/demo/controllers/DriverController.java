package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @GetMapping
    public String getDriverName(){
        return "Ivan";
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
