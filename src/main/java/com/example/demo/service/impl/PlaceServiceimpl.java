package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.model.dto.PlaceDTO;
import com.example.demo.model.dto.SessionDTO;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.entity.Place;
import com.example.demo.model.entity.Session;
import com.example.demo.model.enums.HallStatus;
import com.example.demo.model.enums.PlaceStatus;
import com.example.demo.model.repository.HallRepository;
import com.example.demo.model.repository.PlaceRepository;
import com.example.demo.service.HallService;
import com.example.demo.service.PlaceService;
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
public class PlaceServiceimpl implements PlaceService {

    private final PlaceRepository placeRepository;

    private final HallService hallService;
    private final ObjectMapper mapper;

    @Override
    public PlaceDTO create(PlaceDTO placeDTO) {
        placeRepository.findById(placeDTO.getIdPlace()).ifPresent(
                c -> {throw new CustomException("Место под номером: " + placeDTO.getIdPlace() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Place place = mapper.convertValue(placeDTO, Place.class);
        place.setCreatedAt(LocalDateTime.now());
        Place save = placeRepository.save(place);
        return mapper.convertValue(save, PlaceDTO.class);
    }

    @Override
    public PlaceDTO update(PlaceDTO placeDTO) {
        Place place = getPlace(placeDTO.getIdPlace());

        place.setIdPlace(placeDTO.getIdPlace() == null ? place.getIdPlace() : placeDTO.getIdPlace());
        place.setRowNumber(placeDTO.getRowNumber() == null ? place.getRowNumber() : placeDTO.getRowNumber());
        place.setPlaceNumber(placeDTO.getPlaceNumber() == null ? place.getPlaceNumber() : placeDTO.getPlaceNumber());
        place.setStatus(placeDTO.getStatus() == null ? place.getStatus() : placeDTO.getStatus());
        place.setUpdatedAt(LocalDateTime.now());
        place.setPlaceStatus(PlaceStatus.UPDATED);
        Place save = placeRepository.save(place);
        return mapper.convertValue(save, PlaceDTO.class);
    }

    @Override
    public PlaceDTO get(Long idPlace) {

        Place place = getPlace(idPlace);
        HallDTO hall = mapper.convertValue(place.getHall(), HallDTO.class);
        PlaceDTO result = mapper.convertValue(place, PlaceDTO.class);
        result.setHallDTO(hall);
        return result;
    }

    @Override
    public void delete(Long idPlace) {

        Place place = getPlace(idPlace);
        place.setPlaceStatus(PlaceStatus.DELETED);
        place.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        placeRepository.save(place);

    }

    @Override
    public Place getPlace(Long idPlace) {
        return placeRepository.findById(idPlace)
                .orElseThrow(()-> new CustomException("Место под номером: " + idPlace + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public PlaceDTO addToHall(Long idPlace, Integer numberHall) {
        Hall hall = hallService.getHall(numberHall);
        Place place = getPlace(idPlace);
        place.setHall(hall);
        Place save = placeRepository.save(place);
        PlaceDTO response = mapper.convertValue(save, PlaceDTO.class);
        response.setHallDTO(mapper.convertValue(hall, HallDTO.class));
        return response;
    }

    @Override
    public List<PlaceDTO> getAllPlace(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Place> pageResult = placeRepository.findAll(pageRequest);

        List<PlaceDTO> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, PlaceDTO.class))
                .collect(Collectors.toList());

        return collect;
    }
}
