package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.HallDTORequest;
import com.example.demo.model.dto.HallDTOResponse;
import com.example.demo.model.entity.Cinema;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.enums.HallStatus;
import com.example.demo.model.repository.HallRepository;
import com.example.demo.service.CinemaService;
import com.example.demo.service.HallService;
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
public class HallServiceimpl implements HallService {

    private final HallRepository hallRepository;

    private final CinemaService cinemaService;
    private final ObjectMapper mapper;

    @Override
    public HallDTOResponse create(HallDTORequest hallDTORequest) {
        hallRepository.findByNumberHall(hallDTORequest.getNumberHall()).ifPresent(
                c -> {throw new CustomException("Зал с номером: " + hallDTORequest.getNumberHall() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Hall hall = mapper.convertValue(hallDTORequest, Hall.class);
        hall.setCreatedAt(LocalDateTime.now());
        Hall save = hallRepository.save(hall);
        return mapper.convertValue(save, HallDTOResponse.class);
    }

    @Override
    public HallDTOResponse update(HallDTORequest hallDTORequest) {
        Hall hall = getHall(hallDTORequest.getNumberHall());

        hall.setNumberHall(hallDTORequest.getNumberHall() == null ? hall.getNumberHall() : hallDTORequest.getNumberHall());
        hall.setPlaces(hallDTORequest.getPlaces() == null ? hall.getPlaces() : hallDTORequest.getPlaces());
        hall.setRows(hallDTORequest.getRows() == null ? hall.getRows() : hallDTORequest.getRows());
        hall.setUpdatedAt(LocalDateTime.now());
        hall.setHallStatus(HallStatus.UPDATED);
        Hall save = hallRepository.save(hall);
        return mapper.convertValue(save, HallDTOResponse.class);
    }

    @Override
    public HallDTOResponse get(Integer numberHall) {

        Hall hall = getHall(numberHall);
        CinemaDTO cinema = mapper.convertValue(hall.getCinema(), CinemaDTO.class);
        HallDTOResponse result = mapper.convertValue(hall, HallDTOResponse.class);
        result.setCinemaDTO(cinema);
        return result;
    }

    @Override
    public void delete(Integer numberHall) {

        Hall hall = getHall(numberHall);
        hall.setHallStatus(HallStatus.DELETED);
        hall.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        hallRepository.save(hall);

    }

    @Override
    public Hall getHall(Integer numberHall) {
        return hallRepository.findByNumberHall(numberHall)
                .orElseThrow(()-> new CustomException("Зал с номером: " + numberHall + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public HallDTOResponse addToCinema(Integer numberHall, String nameCinema) {
        Cinema cinema = cinemaService.getCinema(nameCinema);
        Hall hall = getHall(numberHall);
        hall.setCinema(cinema);
        Hall save = hallRepository.save(hall);
        HallDTOResponse response = mapper.convertValue(save, HallDTOResponse.class);
        response.setCinemaDTO(mapper.convertValue(cinema, CinemaDTO.class));
        return response;
    }

    @Override
    public List<HallDTOResponse> getAllHall(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Hall> pageResult = hallRepository.findAll(pageRequest);

        List<HallDTOResponse> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, HallDTOResponse.class))
                .collect(Collectors.toList());

        return collect;
    }
}
