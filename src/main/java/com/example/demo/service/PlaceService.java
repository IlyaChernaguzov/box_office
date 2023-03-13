package com.example.demo.service;

import com.example.demo.model.dto.PlaceDTORequest;
import com.example.demo.model.dto.PlaceDTOResponse;
import com.example.demo.model.entity.Place;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PlaceService {
    PlaceDTOResponse create(PlaceDTORequest placeDTORequest, Long idHall);

    PlaceDTOResponse update(PlaceDTORequest placeDTORequest, Long idHall, Long idPlace);

    PlaceDTOResponse get(Long idPlace);

    void delete(Long idPlace);

    Place getPlace(Long idPlace);

//    PlaceDTOResponse addToHall(Integer placeNumber, Integer numberHall);

    List<PlaceDTORequest> getAllPlace(Integer page, Integer perPage, String sort, Sort.Direction order);

    List<PlaceDTORequest> getAllPlaceByHall (Long idHall);
}
