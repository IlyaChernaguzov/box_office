package com.example.demo.service;

import com.example.demo.model.dto.CarDTORequest;
import com.example.demo.model.dto.CarDTOResponse;

public interface CarService {
    CarDTORequest create(CarDTORequest carDTORequest);

    CarDTORequest update(CarDTORequest carDTORequest);

    CarDTORequest get(String stateNumber);

    void delete(String stateNumber);

    CarDTOResponse addToDriver (String stateNumber, String email);

}
