package com.example.demo.service;

import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.CarDTOResponse;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;

import java.util.List;

public interface CarService {
    CarDTORequest create(CarDTORequest carDTORequest);

    CarDTORequest update(CarDTORequest carDTORequest);

    CarDTOResponse get(String stateNumber);

    void delete(String stateNumber);

    CarDTOResponse addToDriver (String stateNumber, String email);

    ModelMap getAllCars(Integer page, Integer perPage, String sort, Sort.Direction order);
}
