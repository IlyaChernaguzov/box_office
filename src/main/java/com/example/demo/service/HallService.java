package com.example.demo.service;

import com.example.demo.model.dto.HallDTORequest;
import com.example.demo.model.dto.HallDTOResponse;
import com.example.demo.model.entity.Hall;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface HallService {
    HallDTOResponse create(HallDTORequest hallDTORequest);

    HallDTOResponse update(HallDTORequest hallDTORequest);

    HallDTOResponse get(Integer numberHall);

    void delete(Integer numberHall);

    Hall getHall(Integer numberHall);

    HallDTOResponse addToCinema (Integer numberHall, String nameCinema);

    List<HallDTOResponse> getAllHall(Integer page, Integer perPage, String sort, Sort.Direction order);
}
