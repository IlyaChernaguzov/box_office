package com.example.demo.model.repository;

import com.example.demo.model.entity.Cinema;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {
    Optional<Hall> findByIdHall(Long idHall);
    List<Hall> findByCinema(Cinema cinema);
    Optional<Hall> findHallByCinemaAndNumberHall(Cinema cinema, Integer numberHall);
}
