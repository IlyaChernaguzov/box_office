package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.HallDTOCreate;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.model.dto.PlaceDTO;
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
    public HallDTO create(HallDTOCreate hallDTOCreate) {
        if(hallDTOCreate.getNumberHall() == null ||
                hallDTOCreate.getRows() == null ||
                hallDTOCreate.getPlaces() == null){
            throw new CustomException("Поля не могут быть пустыми", HttpStatus.BAD_REQUEST);
        }

        Cinema cinema = cinemaService.getCinema(hallDTOCreate.getIdCinema());
        Integer halls = cinema.getHalls();

        hallRepository.findHallByCinemaAndNumberHall(cinema, hallDTOCreate.getNumberHall())
                .ifPresent(c -> {
                    throw new CustomException("Зал с таким номером уже существует", HttpStatus.BAD_REQUEST);
                });

        if (hallDTOCreate.getNumberHall() > halls ||
                hallDTOCreate.getNumberHall() < 1 ) {
            throw new CustomException("Зал под таким номером невозможен", HttpStatus.BAD_REQUEST);
        }

//        Long idHall = hallDTORequest.getIdHall();
//        if (idHall != null && hallRepository.findHallByIdHall(idHall).isPresent()) {
//            throw new CustomException("Кинотеатр с id: " + idHall + " уже существует", HttpStatus.BAD_REQUEST);
//        }
//        List<Hall> halls = hallRepository.findHallByCinema(cinemaService.getCinema(nameCinema))
//                .stream()
//                .filter(o -> Objects.equals(o.getNumberHall(), hallDTORequest.getNumberHall()))
//                .collect(Collectors.toList());
//
//        if (!halls.isEmpty())
//        {
//            throw new CustomException("Такой зал уже существует", HttpStatus.BAD_REQUEST);
//        }

        Hall hall = new Hall();
        hall.setNumberHall(hallDTOCreate.getNumberHall());
        hall.setPlaces(hallDTOCreate.getPlaces());
        hall.setRows(hallDTOCreate.getRows());
        hall.setCinema(cinema);
        hall.setCreatedAt(LocalDateTime.now());

        Hall save = hallRepository.save(hall);
        HallDTO result = mapper.convertValue(save, HallDTO.class);
        result.setIdHall(save.getIdHall());
        result.setIdCinema(hallDTOCreate.getIdCinema());
        return result;
    }

    @Override
    public HallDTO update(HallDTO hallDTO) {
        Hall hall = getHall(hallDTO.getIdHall());

//        hall.setIdHall(hallDTORequest.getIdHall());
        hall.setNumberHall(hallDTO.getNumberHall() == null ? hall.getNumberHall() : hallDTO.getNumberHall());
        hall.setPlaces(hallDTO.getPlaces() == null ? hall.getPlaces() : hallDTO.getPlaces());
        hall.setRows(hallDTO.getRows() == null ? hall.getRows() : hallDTO.getRows());
        hall.setCinema(hallDTO.getIdCinema() == null ? hall.getCinema() : cinemaService.getCinema(hallDTO.getIdCinema()));
        hall.setUpdatedAt(LocalDateTime.now());
        hall.setHallStatus(HallStatus.UPDATED);
        Hall save = hallRepository.save(hall);

        HallDTO result = mapper.convertValue(save, HallDTO.class);
        result.setIdHall(save.getIdHall());
        result.setIdCinema(save.getCinema().getIdCinema());
        return result;
    }

    @Override
    public HallDTO get(Long idHall) {

        Hall hall = getHall(idHall);
        HallDTO result = mapper.convertValue(hall, HallDTO.class);
        result.setIdHall(idHall);
        result.setIdCinema(hall.getCinema().getIdCinema());
        return result;
    }

    @Override
    public void delete(Long idHall) {

        Hall hall = getHall(idHall);
        hall.setHallStatus(HallStatus.DELETED);
        hall.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        hallRepository.save(hall);
    }

    @Override
    public Hall getHall(Long idHall) {
        return hallRepository.findByIdHall(idHall)
                .orElseThrow(()-> new CustomException("Зал с id: " + idHall + " не найден", HttpStatus.NOT_FOUND));
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
    public List<HallDTO> getAllHall(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Hall> pageResult = hallRepository.findAll(pageRequest);

        List<HallDTO> collect = pageResult.getContent().stream()
                .map(c ->
                {
                    HallDTO response = mapper.convertValue(c, HallDTO.class);
                    response.setIdHall(c.getIdHall());
                    response.setIdCinema(c.getCinema().getIdCinema());

                    return response;
                })
                .collect(Collectors.toList());

        return collect;
    }
}
