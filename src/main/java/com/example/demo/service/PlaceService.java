package com.example.demo.service;

import com.example.demo.model.dto.PlaceDTO;
import com.example.demo.model.dto.PlaceDTOCreate;
import com.example.demo.model.entity.Place;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PlaceService {
    PlaceDTO create(PlaceDTOCreate placeDTOCreate);

    PlaceDTO update(PlaceDTO placeDTO);

    PlaceDTO get(Long idPlace);

    void delete(Long idPlace);

    Place getPlace(Long idPlace);

//    PlaceDTOResponse addToHall(Integer placeNumber, Integer numberHall);

    List<PlaceDTO> getAllPlace(Integer page, Integer perPage, String sort, Sort.Direction order);

    List<PlaceDTO> getAllPlaceByHall (Long idHall);
}
