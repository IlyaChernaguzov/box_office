package com.example.demo.service;

import com.example.demo.model.dto.HallDTORequest;
import com.example.demo.model.dto.HallDTOResponse;
import com.example.demo.model.entity.Hall;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface HallService {
    HallDTOResponse create(HallDTORequest hallDTORequest, String nameCinema);

    HallDTOResponse update(HallDTORequest hallDTORequest, String nameCinema, Long id);

    HallDTOResponse get(Long id);

    void delete(Long id);

    Hall getHall(Long id);

//    HallDTOResponse addToCinema (Integer numberHall, String nameCinema);

    List<HallDTOResponse> getAllHall(Integer page, Integer perPage, String sort, Sort.Direction order);
}
