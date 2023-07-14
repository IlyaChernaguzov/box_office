package com.example.demo.service;

import com.example.demo.model.dto.SessionDTOCreate;
import com.example.demo.model.dto.SessionDTO;
import com.example.demo.model.dto.SessionDTOUpdate;
import com.example.demo.model.entity.Session;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SessionService {
    SessionDTO create(SessionDTOCreate sessionDTOCreate);

    SessionDTO update(SessionDTOUpdate sessionDTOUpdate);

    SessionDTO get(Long idSession);

    void delete(Long idSession);

    Session getSession(Long idSession);

//    SessionDTOResponse addToMovie (String sessionNumber, String nameMovie);
//
//    SessionDTOResponse addToCinema (String sessionNumber, String nameCinema);
//
//    SessionDTOResponse addToHall (String sessionNumber, Integer numberHall);

    List<SessionDTO> getAllSession(Integer page, Integer perPage, String sort, Sort.Direction order);
}
