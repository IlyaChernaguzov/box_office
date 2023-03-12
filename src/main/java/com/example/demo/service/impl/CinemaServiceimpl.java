package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.entity.Cinema;
import com.example.demo.model.enums.CinemaStatus;
import com.example.demo.model.repository.CinemaRepository;
import com.example.demo.service.CinemaService;
import com.example.demo.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaServiceimpl implements CinemaService {

    private final CinemaRepository cinemaRepository;

    private final ObjectMapper mapper;

    @Override
    public CinemaDTO create(CinemaDTO cinemaDTO) {
        cinemaRepository.findByNameCinema(cinemaDTO.getNameCinema()).ifPresent(
                c -> {throw new CustomException("Кинотеатр с названием: " + cinemaDTO.getNameCinema() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Cinema cinema = mapper.convertValue(cinemaDTO, Cinema.class);
        cinema.setCreatedAt(LocalDateTime.now());
        Cinema save = cinemaRepository.save(cinema);
        return mapper.convertValue(save, CinemaDTO.class);
    }

    @Override
    public CinemaDTO update(CinemaDTO cinemaDTO) {

        Cinema cinema = getCinema(cinemaDTO.getNameCinema());

        cinema.setNameCinema(cinemaDTO.getNameCinema() == null ? cinema.getNameCinema() : cinemaDTO.getNameCinema()); // проверяем пришедшие данный на null с помощью "?". ":" - если не null, то присвиваем пришедшее значение
        cinema.setIndex(cinemaDTO.getIndex() == null ? cinema.getIndex() : cinemaDTO.getIndex());
        cinema.setCity(cinemaDTO.getCity() == null ? cinema.getCity() : cinemaDTO.getCity());
        cinema.setAddress(cinemaDTO.getAddress() == null ? cinema.getAddress() : cinemaDTO.getAddress());
        cinema.setHalls(cinemaDTO.getHalls() == null ? cinema.getHalls() : cinemaDTO.getHalls());
        cinema.setUpdatedAt(LocalDateTime.now());
        cinema.setCinemaStatus(CinemaStatus.UPDATED);
        Cinema save = cinemaRepository.save(cinema);
        return mapper.convertValue(save, CinemaDTO.class);
    }

    @Override
    public CinemaDTO get(String nameCinema) {

        Cinema cinema = getCinema(nameCinema);
        return mapper.convertValue(cinema, CinemaDTO.class);

    }

    @Override
    public void delete(String nameCinema) {

        Cinema cinema = getCinema(nameCinema);
        cinema.setCinemaStatus(CinemaStatus.DELETED);
        cinema.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        cinemaRepository.save(cinema);

    }

    @Override
    public Cinema getCinema(String nameCinema) {
        return cinemaRepository.findByNameCinema(nameCinema)
                .orElseThrow(()-> new CustomException("Кинотеатр с названием: " + nameCinema + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<CinemaDTO> getAllCinema(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Cinema> pageResult = cinemaRepository.findAll(pageRequest);

        List<CinemaDTO> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, CinemaDTO.class))
                .collect(Collectors.toList());

        return collect;
    }
}
