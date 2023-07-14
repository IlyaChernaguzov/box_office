package com.example.demo.model.repository;

import com.example.demo.model.entity.Cinema;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.entity.Movie;
import com.example.demo.model.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    Optional<Cinema> findByIdCinema(Long idCinema);

    Optional<Cinema> findByNameCinema(String nameCinema);
}
