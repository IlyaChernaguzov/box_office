package com.example.demo.model.repository;

import com.example.demo.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByStateNumber(String stateNumber); //возвращает не null, а экземпляр Optional

//    Page<Car> findAllByBrandCar(Pageable pageRequest, String brandCar);// запрос в бд на получение отсортированной страницы по брэнду ТС
}
