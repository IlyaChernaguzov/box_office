package com.example.demo.service;

import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.entity.Driver;

public interface DriverService {

    DriverDTO create(DriverDTO driverDTO);

    DriverDTO update(DriverDTO driverDTO);

    DriverDTO get(String email);

    void delete(String email);

    Driver getDriver(String email);
}
