package com.example.demo.service;

import com.example.demo.model.dto.HallDTOCreate;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.model.entity.Hall;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface HallService {
    HallDTO create(HallDTOCreate hallDTOCreate);

    HallDTO update(HallDTO hallDTO);

    HallDTO get(Long idHall);

    void delete(Long idHall);

    Hall getHall(Long idHall);

//    HallDTOResponse addToCinema (Integer numberHall, String nameCinema);

    List<HallDTO> getAllHall(Integer page, Integer perPage, String sort, Sort.Direction order);
}
