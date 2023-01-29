package com.example.demo.model.dto;

import com.example.demo.model.enums.Colors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarDTORequest {

    Colors colorsCar;
    String carDate;
    String brandCar;
    String modelCar;
    String stateNumber;

}
