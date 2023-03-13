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
import java.util.Objects;
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
    public PlaceDTOResponse create(PlaceDTORequest placeDTORequest, Long id) {
        placeRepository
                .findPlaceByHallAndRowNumberAndPlaceNumberInRow(
                        hallService.getHall(id),
                        placeDTORequest.getRowNumber(),
                        placeDTORequest.getPlaceNumberInRow())
                .ifPresent(
                        c -> {throw new CustomException("Такое место уже существует", HttpStatus.BAD_REQUEST);
                        });

        if (placeDTORequest.getRowNumber() > hallService.getHall(id).getRows() ||
                placeDTORequest.getRowNumber() < 1 ||
                placeDTORequest.getRowNumber() == null)
        {
            throw new CustomException("Номер ряда не может быть больше " + hallService.getHall(id).getRows(), HttpStatus.BAD_REQUEST);
        }

        if (placeDTORequest.getPlaceNumberInRow() > hallService.getHall(id).getPlaces() ||
                placeDTORequest.getPlaceNumberInRow() < 1 ||
                placeDTORequest.getPlaceNumberInRow() == null)
        {
            throw new CustomException("Номер места не может быть больше " + hallService.getHall(id).getPlaces(), HttpStatus.BAD_REQUEST);
        }

//        List<Place> places = placeRepository.findByHall(hallService.getHall(id))
//                .stream()
//                .filter(o -> Objects.equals(o.getRowNumber(), placeDTORequest.getRowNumber()))
//                .filter(p -> Objects.equals(p.getPlaceNumberInRow(), placeDTORequest.getPlaceNumberInRow()))
//                .collect(Collectors.toList());
//
//        if (!places.isEmpty())
//        {
//            throw new CustomException("Такое место уже существует", HttpStatus.BAD_REQUEST);
//        }

//        placeRepository.findByPlaceNumber(placeDTORequest.getPlaceNumber()).ifPresent(
//                c -> {throw new CustomException("Место под номером: " + placeDTORequest.getPlaceNumber() + " уже существует", HttpStatus.BAD_REQUEST);
//                }
//        );

        Place place = mapper.convertValue(placeDTORequest, Place.class);
        place.setHall(hallService.getHall(id));
        place.setCreatedAt(LocalDateTime.now());
        Place save = placeRepository.save(place);
        return get(save.getIdPlace());
    }

    @Override
    public PlaceDTOResponse update(PlaceDTORequest placeDTORequest, Long id, Long idPlace) {
        Place place = getPlace(idPlace);

//        place.setPlaceNumber(placeDTORequest.getPlaceNumber() == null ? place.getPlaceNumber() : placeDTORequest.getPlaceNumber());
        place.setRowNumber(placeDTORequest.getRowNumber() == null ? place.getRowNumber() : placeDTORequest.getRowNumber());
        place.setPlaceNumberInRow(placeDTORequest.getPlaceNumberInRow() == null ? place.getPlaceNumberInRow() : placeDTORequest.getPlaceNumberInRow());
        place.setHall(id == null ? place.getHall() : hallService.getHall(id));
        place.setUpdatedAt(LocalDateTime.now());
        place.setPlaceStatus(PlaceStatus.UPDATED);
        Place save = placeRepository.save(place);
        return get(save.getIdPlace());
    }

    @Override
    public PlaceDTOResponse get(Long idPlace) {

        Place place = getPlace(idPlace);
        HallDTORequest hall = mapper.convertValue(place.getHall(), HallDTORequest.class);
        PlaceDTOResponse result = mapper.convertValue(place, PlaceDTOResponse.class);
        result.setHallDTORequest(hall);
        result.setIdPlace(idPlace);
        return result;
    }

    @Override
    public void delete(Long idPlace) {

        Place place = getPlace(idPlace);
        place.setPlaceStatus(PlaceStatus.DELETED);
        place.setUpdatedAt(LocalDateTime.now());
//        placeRepository.delete(place);// полное удаление
        placeRepository.save(place);
    }

    @Override
    public Place getPlace(Long idPlace) {
        return placeRepository.findByIdPlace(idPlace)
                .orElseThrow(()-> new CustomException("Место под номером: " + idPlace + " не найден", HttpStatus.NOT_FOUND));
    }

//    @Override
//    public PlaceDTOResponse addToHall(Integer placeNumber, Long idHall) {
//        Hall hall = hallService.getHall(idHall);
//        Place place = getPlace(placeNumber);
//        place.setHall(hall);
//        Place save = placeRepository.save(place);
//        PlaceDTOResponse response = mapper.convertValue(save, PlaceDTOResponse.class);
//        response.setHallDTORequest(mapper.convertValue(hall, HallDTORequest.class));
//        return response;
//    }

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
    public List<PlaceDTORequest> getAllPlaceByHall(Long id) {

        List<Place> places = placeRepository.findByHall(hallService.getHall(id));

        List<PlaceDTORequest> response = places.stream()
                .map(r -> mapper.convertValue(r, PlaceDTORequest.class))
                .collect(Collectors.toList());

        return response;
    }
}
