package com.example.demo.model.entity;

import com.example.demo.model.enums.CinemaStatus;
import com.example.demo.model.enums.MovieStatus;
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
@Table(name = "cinemas")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cinema {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idCinema;

    @Column(name = "name_cinema", unique = true)
    String nameCinema;

    @Column(name = "index_cinema")
    String index;

    @Column(name = "city_cinema")
    String city;

    @Column(name = "address_cinema")
    String address;

    @Column(name = "quantity_halls")
    Integer halls;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    CinemaStatus cinemaStatus = CinemaStatus.CREATE;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Session> session;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Hall> hall;

}
