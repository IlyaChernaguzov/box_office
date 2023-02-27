package com.example.demo.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HallDTO {

    Integer numberHall;
    Integer places;
    Integer rows;
}
