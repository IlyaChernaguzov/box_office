package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.enums.HallStatus;
import com.example.demo.model.repository.HallRepository;
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
    private final ObjectMapper mapper;

    @Override
    public HallDTO create(HallDTO hallDTO) {
        hallRepository.findByNumberHall(hallDTO.getNumberHall()).ifPresent(
                c -> {throw new CustomException("Зал с номером: " + hallDTO.getNumberHall() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Hall hall = mapper.convertValue(hallDTO, Hall.class);
        hall.setCreatedAt(LocalDateTime.now());
        Hall save = hallRepository.save(hall);
        return mapper.convertValue(save, HallDTO.class);
    }

    @Override
    public HallDTO update(HallDTO hallDTO) {
        Hall hall = getHall(hallDTO.getNumberHall());

        hall.setNumberHall(hallDTO.getNumberHall() == null ? hall.getNumberHall() : hallDTO.getNumberHall());
        hall.setPlaces(hallDTO.getPlaces() == null ? hall.getPlaces() : hallDTO.getPlaces());
        hall.setRows(hallDTO.getRows() == null ? hall.getRows() : hallDTO.getRows());
        hall.setUpdatedAt(LocalDateTime.now());
        hall.setHallStatus(HallStatus.UPDATED);
        Hall save = hallRepository.save(hall);
        return mapper.convertValue(save, HallDTO.class);
    }

    @Override
    public HallDTO get(Integer numberHall) {

        Hall hall = getHall(numberHall);
        return mapper.convertValue(hall, HallDTO.class);
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
    public List<HallDTO> getAllHall(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Hall> pageResult = hallRepository.findAll(pageRequest);

        List<HallDTO> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, HallDTO.class))
                .collect(Collectors.toList());

        return collect;
    }
}
