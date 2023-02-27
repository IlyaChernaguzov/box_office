package com.example.demo.service;

import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.entity.Place;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface HallService {
    HallDTO create(HallDTO hallDTO);

    HallDTO update(HallDTO hallDTO);

    HallDTO get(Integer numberHall);

    void delete(Integer numberHall);

    Hall getHall(Integer numberHall);

    List<HallDTO> getAllHall(Integer page, Integer perPage, String sort, Sort.Direction order);
}
