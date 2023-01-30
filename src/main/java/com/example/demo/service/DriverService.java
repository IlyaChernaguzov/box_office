package com.example.demo.service;

import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.entity.Driver;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface DriverService {

    DriverDTO create(DriverDTO driverDTO);

    DriverDTO update(DriverDTO driverDTO);

    DriverDTO get(String email);

    void delete(String email);

    Driver getDriver(String email);

    List<DriverDTO> getAllDrivers(Integer page, Integer perPage, String sort, Sort.Direction order);
}
