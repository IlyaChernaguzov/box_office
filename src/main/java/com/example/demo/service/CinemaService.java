package com.example.demo.service;

import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.entity.Cinema;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CinemaService {
    CinemaDTO create(CinemaDTO cinemaDTO);

    CinemaDTO update(CinemaDTO cinemaDTO);

    CinemaDTO get(String nameCinema);

    void delete(String nameCinema);

    Cinema getCinema(String nameCinema);

    List<CinemaDTO> getAllCinema(Integer page, Integer perPage, String sort, Sort.Direction order);
}
