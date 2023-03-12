package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.HallDTORequest;
import com.example.demo.model.dto.PlaceDTORequest;
import com.example.demo.model.dto.PlaceDTOResponse;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.entity.Place;
import com.example.demo.model.enums.PlaceStatus;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceServiceimpl implements PlaceService {

    private final PlaceRepository placeRepository;

    private final HallService hallService;
    private final ObjectMapper mapper;

    @Override
    public PlaceDTOResponse create(PlaceDTORequest placeDTORequest) {
        placeRepository.findByPlaceNumber(placeDTORequest.getPlaceNumber()).ifPresent(
                c -> {throw new CustomException("Место под номером: " + placeDTORequest.getPlaceNumber() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Place place = mapper.convertValue(placeDTORequest, Place.class);
        place.setCreatedAt(LocalDateTime.now());
        Place save = placeRepository.save(place);
        return mapper.convertValue(save, PlaceDTOResponse.class);
    }

    @Override
    public PlaceDTOResponse update(PlaceDTORequest placeDTORequest) {
        Place place = getPlace(placeDTORequest.getPlaceNumber());

        place.setPlaceNumber(placeDTORequest.getPlaceNumber() == null ? place.getPlaceNumber() : placeDTORequest.getPlaceNumber());
        place.setRowNumber(placeDTORequest.getRowNumber() == null ? place.getRowNumber() : placeDTORequest.getRowNumber());
        place.setPlaceNumberInRow(placeDTORequest.getPlaceNumberInRow() == null ? place.getPlaceNumberInRow() : placeDTORequest.getPlaceNumberInRow());
//        place.setBooking(placeDTORequest.getBooking() == null ? place.getBooking() : placeDTORequest.getBooking());
        place.setUpdatedAt(LocalDateTime.now());
        place.setPlaceStatus(PlaceStatus.UPDATED);
        Place save = placeRepository.save(place);
        return mapper.convertValue(save, PlaceDTOResponse.class);
    }

    @Override
    public PlaceDTOResponse get(Integer placeNumber) {

        Place place = getPlace(placeNumber);
        HallDTORequest hall = mapper.convertValue(place.getHall(), HallDTORequest.class);
        PlaceDTOResponse result = mapper.convertValue(place, PlaceDTOResponse.class);
        result.setHallDTORequest(hall);
        return result;
    }

    @Override
    public void delete(Integer placeNumber) {

        Place place = getPlace(placeNumber);
        place.setPlaceStatus(PlaceStatus.DELETED);
        place.setUpdatedAt(LocalDateTime.now());
//        placeRepository.delete(place);// полное удаление
        placeRepository.save(place);
    }

    @Override
    public Place getPlace(Integer placeNumber) {
        return placeRepository.findByPlaceNumber(placeNumber)
                .orElseThrow(()-> new CustomException("Место под номером: " + placeNumber + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public PlaceDTOResponse addToHall(Integer placeNumber, Integer numberHall) {
        Hall hall = hallService.getHall(numberHall);
        Place place = getPlace(placeNumber);
        place.setHall(hall);
        Place save = placeRepository.save(place);
        PlaceDTOResponse response = mapper.convertValue(save, PlaceDTOResponse.class);
        response.setHallDTORequest(mapper.convertValue(hall, HallDTORequest.class));
        return response;
    }

    @Override
    public List<PlaceDTORequest> getAllPlace(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Place> pageResult = placeRepository.findAll(pageRequest);

        List<PlaceDTORequest> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, PlaceDTORequest.class))
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<PlaceDTORequest> getAllPlaceByHall(Integer numberHall) {

        List<Place> places = placeRepository.findByHall(hallService.getHall(numberHall));
//        HallDTORequest hall = mapper.convertValue(hallService.getHall(numberHall), HallDTORequest.class);

        List<PlaceDTORequest> response = places.stream()
                .map(r -> mapper.convertValue(r, PlaceDTORequest.class))
                .collect(Collectors.toList());

        return response;
    }
}
