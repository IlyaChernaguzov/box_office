package com.example.demo.service;

import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.CinemaDTOCreate;
import com.example.demo.model.entity.Cinema;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CinemaService {
    CinemaDTO create(CinemaDTOCreate cinemaDTOCreate);

    CinemaDTO update(CinemaDTO cinemaDTO);

    CinemaDTO get(Long idCinema);

    void delete(Long idCinema);

    Cinema getCinema(Long idCinema);

    List<CinemaDTO> getAllCinema(Integer page, Integer perPage, String sort, Sort.Direction order);
}
