package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.OrderDTOResponse;
import com.example.demo.model.dto.PlaceDTO;
import com.example.demo.model.dto.PlaceDTOCreate;
import com.example.demo.model.dto.UserDTO;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceServiceimpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final HallService hallService;
    private final ObjectMapper mapper;

    @Override
    public PlaceDTO create(PlaceDTOCreate placeDTOCreate) {

        if (placeDTOCreate.getRowNumber() == null) {
            throw new CustomException("Поле RowNumber не может быть пустым", HttpStatus.BAD_REQUEST);
        }
        if (placeDTOCreate.getPlaceNumberInRow() == null) {
            throw new CustomException("Поле PlaceNumberInRow не может быть пустым", HttpStatus.BAD_REQUEST);
        }


        Hall hall = hallService.getHall(placeDTOCreate.getIdHall());
        Integer rows = hall.getRows();
        Integer places = hall.getPlaces();

        placeRepository
                .findPlaceByHallAndRowNumberAndPlaceNumberInRow(
                        hall,
                        placeDTOCreate.getRowNumber(),
                        placeDTOCreate.getPlaceNumberInRow())
                .ifPresent(c -> {
                    throw new CustomException("Такое место уже существует", HttpStatus.BAD_REQUEST);
                });

        if (placeDTOCreate.getRowNumber() > rows ||
                placeDTOCreate.getRowNumber() < 1) {
            throw new CustomException("Неправильный номер ряда ", HttpStatus.BAD_REQUEST);
        }

        if (placeDTOCreate.getPlaceNumberInRow() > places ||
                placeDTOCreate.getPlaceNumberInRow() < 1) {
            throw new CustomException("Неправильный номер места ", HttpStatus.BAD_REQUEST);
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

        Place place = new Place();
        place.setRowNumber(placeDTOCreate.getRowNumber());
        place.setPlaceNumberInRow(placeDTOCreate.getPlaceNumberInRow());
        place.setHall(hall);
        place.setCreatedAt(LocalDateTime.now());

        Place save = placeRepository.save(place);
        return get(save.getIdPlace());
    }

    @Override
    public PlaceDTO update(PlaceDTO placeDTO) {
        Place place = getPlace(placeDTO.getIdPlace());

//        place.setPlaceNumber(placeDTORequest.getPlaceNumber() == null ? place.getPlaceNumber() : placeDTORequest.getPlaceNumber());
        place.setRowNumber(placeDTO.getRowNumber() == null ? place.getRowNumber() : placeDTO.getRowNumber());
        place.setPlaceNumberInRow(placeDTO.getPlaceNumberInRow() == null ? place.getPlaceNumberInRow() : placeDTO.getPlaceNumberInRow());
        place.setHall(placeDTO.getIdHall() == null ? place.getHall() : hallService.getHall(placeDTO.getIdHall()));

        if (place.getRowNumber() > place.getHall().getRows() ||
                place.getRowNumber() < 1) {
            throw new CustomException("Неправильный номер ряда ", HttpStatus.BAD_REQUEST);
        }

        if (place.getPlaceNumberInRow() > place.getHall().getPlaces() ||
                place.getPlaceNumberInRow() < 1) {
            throw new CustomException("Неправильный номер места ", HttpStatus.BAD_REQUEST);
        }

        place.setUpdatedAt(LocalDateTime.now());
        place.setPlaceStatus(PlaceStatus.UPDATED);
        Place save = placeRepository.save(place);
        return get(save.getIdPlace());
    }

    @Override
    public PlaceDTO get(Long idPlace) {

        Place place = getPlace(idPlace);
        PlaceDTO result = mapper.convertValue(place, PlaceDTO.class);
        result.setIdPlace(idPlace);
        result.setIdHall(place.getHall().getIdHall());
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
        return placeRepository
                .findByIdPlace(idPlace)
                .orElseThrow(()-> new CustomException("Место с id: " + idPlace + " не найдено", HttpStatus.NOT_FOUND));
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
    public List<PlaceDTO> getAllPlace(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Place> pageResult = placeRepository.findAll(pageRequest);

        List<PlaceDTO> collect = pageResult.getContent().stream()
                .map(c ->
                {
                    PlaceDTO response = mapper.convertValue(c, PlaceDTO.class);
                    response.setIdPlace(c.getIdPlace());
                    response.setIdHall(c.getHall().getIdHall());

                return response;
                })
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<PlaceDTO> getAllPlaceByHall(Long idHall) {

        List<Place> places = placeRepository.findByHall(hallService.getHall(idHall));

        List<PlaceDTO> orders = places.stream()
                .map(o ->
                {
                    PlaceDTO response = mapper.convertValue(o, PlaceDTO.class);
                    response.setIdPlace(o.getIdPlace());
                    response.setIdHall(o.getHall().getIdHall());

                    return response;
                })
                .collect(Collectors.toList());

        return orders;
    }
}
