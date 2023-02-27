package com.example.demo.model.repository;

import com.example.demo.model.entity.Movie;
import com.example.demo.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findById(Long idSession);
}
