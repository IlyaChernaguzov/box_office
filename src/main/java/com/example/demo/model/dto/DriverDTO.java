package com.example.demo.model.dto;

import com.example.demo.model.enums.Genre;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverDTO {

    String name;
    String surname;
    Genre gender;
    String email;

}
