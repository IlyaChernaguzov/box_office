package com.example.demo.model.repository;

import com.example.demo.model.entity.Movie;
import com.example.demo.model.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long idPlace);
}
