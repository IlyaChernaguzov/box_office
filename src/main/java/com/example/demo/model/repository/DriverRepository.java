package com.example.demo.model.repository;

import com.example.demo.model.entity.Car;
import com.example.demo.model.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByEmail(String email); //возвращает не null, а экземпляр Optional

//    List<Driver> findAllByName(String name); // поиск через метод репозитория

//    @Query("select driver from Driver driver where driver.name = :name")
//    List<Driver> getDrivers(@Param("name") String name); // поиск через HQL

//    @Query(value = "select * from drivers where drivers.name = :name", nativeQuery = true)
//    List<Driver> getDriversNative(@Param("name") String name); // поиск через SQL
}
