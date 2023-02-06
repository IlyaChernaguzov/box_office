package com.example.demo.model.entity;


import com.example.demo.model.enums.CarStatus;
import com.example.demo.model.enums.DriverStatus;
import com.example.demo.model.enums.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "drivers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "sur_name")
    String surname;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "email", unique = true)
    String email;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    DriverStatus status = DriverStatus.CREATE;

    @OneToMany(cascade = CascadeType.ALL)
    List<Car> cars;

}
