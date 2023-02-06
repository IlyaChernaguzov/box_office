package com.example.demo.model.entity;

import com.example.demo.model.enums.CarStatus;
import com.example.demo.model.enums.Colors;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cars")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Car {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "colors_car")
    @Enumerated(EnumType.STRING)
    Colors colorsCar;

    @Column(name = "car_old")
    String carOld;

    @Column(name = "brand_car")
    String brandCar;

    @Column(name = "model_car")
    String modelCar;

    @Column(name = "state_number", unique = true)
    String stateNumber;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    CarStatus status = CarStatus.CREATE;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    Driver driver;

}
