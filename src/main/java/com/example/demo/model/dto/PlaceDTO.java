package com.example.demo.model.dto;

import com.example.demo.model.enums.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaceDTO {

    Long idPlace;
    Integer rowNumber;
    Integer placeNumber;
    Status status;
    HallDTO hallDTO;
}
