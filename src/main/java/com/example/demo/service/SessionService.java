package com.example.demo.service;

import com.example.demo.model.dto.SessionDTORequest;
import com.example.demo.model.dto.SessionDTOResponse;
import com.example.demo.model.dto.SessionDTOUpdate;
import com.example.demo.model.entity.Session;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SessionService {
    SessionDTOResponse create(SessionDTORequest sessionDTORequest);

    SessionDTOResponse update(SessionDTOUpdate sessionDTOUpdate);

    SessionDTOResponse get(String sessionNumber);

    void delete(String sessionNumber);

    Session getSession(String sessionNumber);

    SessionDTOResponse addToMovie (String sessionNumber, String nameMovie);

    SessionDTOResponse addToCinema (String sessionNumber, String nameCinema);

    SessionDTOResponse addToHall (String sessionNumber, Integer numberHall);

    List<SessionDTOResponse> getAllSession(Integer page, Integer perPage, String sort, Sort.Direction order);
}
