package com.example.demo.model.repository;

import com.example.demo.model.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByStateNumber(String stateNumber); //возвращает не null, а экземпляр Optional

}
