package com.example.demo.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaDTOCreate {

    String nameCinema;
    String index;
    String city;
    String address;
    Integer halls;
}
