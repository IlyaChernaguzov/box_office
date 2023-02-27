package com.example.demo.model.dto;

import com.example.demo.model.enums.Rating;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarDTORequest {

    Rating colorsCar;
    String carOld;
    String brandCar;
    String modelCar;
    String stateNumber;

}
