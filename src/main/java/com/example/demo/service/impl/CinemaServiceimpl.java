package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.CinemaDTOCreate;
import com.example.demo.model.entity.Cinema;
import com.example.demo.model.enums.CinemaStatus;
import com.example.demo.model.repository.CinemaRepository;
import com.example.demo.service.CinemaService;
import com.example.demo.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public CinemaDTO create(CinemaDTOCreate cinemaDTOCreate) {
        if (StringUtils.isBlank(cinemaDTOCreate.getNameCinema())) {
            throw new CustomException("Поле не может быть пустым", HttpStatus.BAD_REQUEST);
        }

        if (cinemaDTOCreate.getHalls() == null) {
            throw new CustomException("Количество залов не может быть пустым", HttpStatus.BAD_REQUEST);
        }

        cinemaRepository.findByNameCinema(cinemaDTOCreate.getNameCinema()).ifPresent(
                c -> {throw new CustomException("Кинотеатр с названием: " + cinemaDTOCreate.getNameCinema() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

//        Long id = cinemaDTO.getIdCinema();
//        if (id != null && cinemaRepository.findById(id).isPresent()) {
//            throw new CustomException("Кинотеатр с id: " + id + " уже существует", HttpStatus.BAD_REQUEST);
//        }

        Cinema cinema = mapper.convertValue(cinemaDTOCreate, Cinema.class);
        cinema.setCreatedAt(LocalDateTime.now());
        Cinema save = cinemaRepository.save(cinema);
//        CinemaDTO result = mapper.convertValue(save, CinemaDTO.class);
//        result.setIdCinema(save.getIdCinema());
//        return result;
        return get(save.getIdCinema());
    }

    @Override
    public CinemaDTO update(CinemaDTO cinemaDTO) {

        Cinema cinema = getCinema(cinemaDTO.getIdCinema());

        cinema.setIdCinema(cinemaDTO.getIdCinema());
        cinema.setNameCinema(StringUtils.isBlank(cinemaDTO.getNameCinema()) ? cinema.getNameCinema() : cinemaDTO.getNameCinema());
        cinema.setIndex(StringUtils.isBlank(cinemaDTO.getIndex()) ? cinema.getIndex() : cinemaDTO.getIndex());
        cinema.setCity(StringUtils.isBlank(cinemaDTO.getCity()) ? cinema.getCity() : cinemaDTO.getCity());
        cinema.setAddress(StringUtils.isBlank(cinemaDTO.getAddress()) ? cinema.getAddress() : cinemaDTO.getAddress());
        cinema.setHalls(cinemaDTO.getHalls() == null ? cinema.getHalls() : cinemaDTO.getHalls());
        cinema.setUpdatedAt(LocalDateTime.now());
        cinema.setCinemaStatus(CinemaStatus.UPDATED);
        Cinema save = cinemaRepository.save(cinema);
//        CinemaDTO result = mapper.convertValue(save, CinemaDTO.class);
//        result.setIdCinema(save.getIdCinema());
//        return result;
        return get(save.getIdCinema());
    }

    @Override
    public CinemaDTO get(Long idCinema) {

        Cinema cinema = getCinema(idCinema);
        CinemaDTO result = mapper.convertValue(cinema, CinemaDTO.class);
        result.setIdCinema(idCinema);
        return result;
    }

    @Override
    public void delete(Long idCinema) {

        Cinema cinema = getCinema(idCinema);
        cinema.setCinemaStatus(CinemaStatus.DELETED);
        cinema.setUpdatedAt(LocalDateTime.now());
        //cinemaRepository.delete(cinema);// полное удаление
        cinemaRepository.save(cinema);

    }

    @Override
    public Cinema getCinema(Long idCinema) {
        return cinemaRepository.findByIdCinema(idCinema)
                .orElseThrow(()-> new CustomException("Кинотеатр с id: " + idCinema + " не найден", HttpStatus.NOT_FOUND));
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
