package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.HallDTORequest;
import com.example.demo.model.dto.HallDTOResponse;
import com.example.demo.model.entity.Cinema;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.entity.Order;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HallServiceimpl implements HallService {

    private final HallRepository hallRepository;
    private final CinemaService cinemaService;
    private final ObjectMapper mapper;

    @Override
    public HallDTOResponse create(HallDTORequest hallDTORequest, String nameCinema) {

        hallRepository
                .findHallByCinemaAndNumberHall(cinemaService.getCinema(nameCinema), hallDTORequest.getNumberHall())
                .ifPresent(
                        c -> {throw new CustomException("Зал с таким номером уже существует", HttpStatus.BAD_REQUEST);
                        });

        if (hallDTORequest.getNumberHall() > cinemaService.getCinema(nameCinema).getHalls() ||
                hallDTORequest.getNumberHall() < 1 || hallDTORequest.getNumberHall() == null)
        {
            throw new CustomException("Зал под таким номером невозможен", HttpStatus.BAD_REQUEST);
        }
//        List<Hall> halls = hallRepository.findHallByCinema(cinemaService.getCinema(nameCinema))
//                .stream()
//                .filter(o -> Objects.equals(o.getNumberHall(), hallDTORequest.getNumberHall()))
//                .collect(Collectors.toList());
//
//        if (!halls.isEmpty())
//        {
//            throw new CustomException("Такой зал уже существует", HttpStatus.BAD_REQUEST);
//        }

        Hall hall = mapper.convertValue(hallDTORequest, Hall.class);
        hall.setCinema(cinemaService.getCinema(nameCinema));
        hall.setCreatedAt(LocalDateTime.now());
        Hall save = hallRepository.save(hall);
        return get(save.getId());
    }

    @Override
    public HallDTOResponse update(HallDTORequest hallDTORequest, String nameCinema, Long id) {
        Hall hall = getHall(id);

        hall.setNumberHall(hallDTORequest.getNumberHall() == null ? hall.getNumberHall() : hallDTORequest.getNumberHall());
        hall.setPlaces(hallDTORequest.getPlaces() == null ? hall.getPlaces() : hallDTORequest.getPlaces());
        hall.setRows(hallDTORequest.getRows() == null ? hall.getRows() : hallDTORequest.getRows());
        hall.setCinema(nameCinema == null ? hall.getCinema() : cinemaService.getCinema(nameCinema));
        hall.setUpdatedAt(LocalDateTime.now());
        hall.setHallStatus(HallStatus.UPDATED);
        Hall save = hallRepository.save(hall);
        return get(save.getId());
    }

    @Override
    public HallDTOResponse get(Long id) {

        Hall hall = getHall(id);
        CinemaDTO cinema = mapper.convertValue(hall.getCinema(), CinemaDTO.class);
        HallDTOResponse result = mapper.convertValue(hall, HallDTOResponse.class);
        result.setCinemaDTO(cinema);
        return result;
    }

    @Override
    public void delete(Long id) {

        Hall hall = getHall(id);
        hall.setHallStatus(HallStatus.DELETED);
        hall.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        hallRepository.save(hall);

    }

    @Override
    public Hall getHall(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(()-> new CustomException("Зал не найден", HttpStatus.NOT_FOUND));
    }

//    @Override
//    public HallDTOResponse addToCinema(Integer numberHall, String nameCinema) {
//        Cinema cinema = cinemaService.getCinema(nameCinema);
//        Hall hall = getHall(numberHall);
//        hall.setCinema(cinema);
//        Hall save = hallRepository.save(hall);
//        HallDTOResponse response = mapper.convertValue(save, HallDTOResponse.class);
//        response.setCinemaDTO(mapper.convertValue(cinema, CinemaDTO.class));
//        return response;
//    }

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
