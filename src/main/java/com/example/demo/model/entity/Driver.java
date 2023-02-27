package com.example.demo.model.entity;


import com.example.demo.model.enums.DriverStatus;
import com.example.demo.model.enums.Genre;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "sur_name")
    String surname;

    @Enumerated(EnumType.STRING)
    Genre gender;

    @Column(name = "email", unique = true)
    String email;

    @CreationTimestamp
    @JsonIgnore
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    DriverStatus status = DriverStatus.CREATE;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Movie> cars;

}
