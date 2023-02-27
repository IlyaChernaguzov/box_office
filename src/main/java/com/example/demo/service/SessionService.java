package com.example.demo.service;

import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.dto.OrderDTO;
import com.example.demo.model.dto.SessionDTO;
import com.example.demo.model.entity.Place;
import com.example.demo.model.entity.Session;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SessionService {
    SessionDTO create(SessionDTO sessionDTO);

    SessionDTO update(SessionDTO sessionDTO);

    SessionDTO get(Long idSession);

    void delete(Long idSession);

    Session getSession(Long idSession);

    SessionDTO addToMovie (Long idSession, String nameMovie);

    SessionDTO addToCinema (Long idSession, String nameCinema);

    SessionDTO addToHall (Long idSession, Integer numberHall);

    List<SessionDTO> getAllSession(Integer page, Integer perPage, String sort, Sort.Direction order);
}
