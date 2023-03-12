package com.example.demo.model.repository;

import com.example.demo.model.entity.Cinema;
import com.example.demo.model.entity.Movie;
import com.example.demo.model.entity.Order;
import com.example.demo.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionNumber(String sessionNumber);

    List<Session> findByCinema(Cinema cinema);

}
