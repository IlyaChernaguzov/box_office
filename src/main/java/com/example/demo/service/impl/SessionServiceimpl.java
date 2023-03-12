package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.SessionStatus;
import com.example.demo.model.repository.SessionRepository;
import com.example.demo.service.CinemaService;
import com.example.demo.service.HallService;
import com.example.demo.service.MovieService;
import com.example.demo.service.SessionService;
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
public class SessionServiceimpl implements SessionService {

    private final SessionRepository sessionRepository;
//    private final MovieRepository movieRepository;
//    private final CinemaRepository cinemaRepository;
//    private final HallRepository hallRepository;
    private final MovieService movieService;
    private final CinemaService cinemaService;
    private final HallService hallService;
    private final ObjectMapper mapper;

    @Override
    public SessionDTOResponse create(SessionDTORequest sessionDTORequest) {
        sessionRepository.findBySessionNumber(sessionDTORequest.getSessionNumber()).ifPresent(
                c -> {throw new CustomException("Сеанс под номером: " + sessionDTORequest.getSessionNumber() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Session session = mapper.convertValue(sessionDTORequest, Session.class);
        session.setCreatedAt(LocalDateTime.now());
        Session save = sessionRepository.save(session);
        return mapper.convertValue(save, SessionDTOResponse.class);
    }

    @Override
    public SessionDTOResponse update(SessionDTOUpdate sessionDTOUpdate) {
        Session session = getSession(sessionDTOUpdate.getSessionNumber());

        session.setSessionNumber(sessionDTOUpdate.getSessionNumber() == null ? session.getSessionNumber() : sessionDTOUpdate.getSessionNumber());// ВОПРОС!
        session.setStartSession(sessionDTOUpdate.getStartSession() == null ? session.getStartSession() : sessionDTOUpdate.getStartSession());
        session.setPrice(sessionDTOUpdate.getPrice() == null ? session.getPrice() : sessionDTOUpdate.getPrice());
        session.setMovie(sessionDTOUpdate.getNameMovie() == null ? session.getMovie() : movieService.getMovie(sessionDTOUpdate.getNameMovie()));
        session.setCinema(sessionDTOUpdate.getNameCinema() == null ? session.getCinema() : cinemaService.getCinema(sessionDTOUpdate.getNameCinema()));
        session.setHall(sessionDTOUpdate.getNumberHall() == null ? session.getHall() : hallService.getHall(sessionDTOUpdate.getNumberHall()));// ВОПРОС!
//        order.setUser(orderDTO.getEmail() == null ? order.getUser() : userService.getUser(orderDTO.getEmail()));
//        order.setPlace(orderDTO.getPlaceNumber() == null ? order.getPlace() : placeService.getPlace(orderDTO.getPlaceNumber()));
//        order.setSession(orderDTO.getSessionNumber() == null ? order.getSession() : sessionService.getSession(orderDTO.getSessionNumber()));
        session.setUpdatedAt(LocalDateTime.now());
        session.setSessionStatus(SessionStatus.UPDATED);
        Session save = sessionRepository.save(session);
        SessionDTOResponse response = get(save.getSessionNumber());

        return response;
    }

    @Override
    public SessionDTOResponse get(String sessionNumber) {
        Session session = getSession(sessionNumber);
        MovieDTO movie = mapper.convertValue(session.getMovie(), MovieDTO.class);
        CinemaDTO cinema = mapper.convertValue(session.getCinema(), CinemaDTO.class);
        HallDTORequest hall = mapper.convertValue(session.getHall(), HallDTORequest.class);
        SessionDTOResponse result = mapper.convertValue(session, SessionDTOResponse.class);
        result.setMovieDTO(movie);
        result.setCinemaDTO(cinema);
        result.setHallDTORequest(hall);// ВОПРОС!

        return result;
    }

    @Override
    public void delete(String sessionNumber) {

        Session session = getSession(sessionNumber);

        session.setSessionStatus(SessionStatus.DELETED);
        session.setUpdatedAt(LocalDateTime.now());
//        carRepository.delete(car);// полное удаление
        sessionRepository.save(session);

    }

    @Override
    public Session getSession(String sessionNumber) {
        return sessionRepository.findBySessionNumber(sessionNumber)
                .orElseThrow(()-> new CustomException("Сеанс под номером " + sessionNumber + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public SessionDTOResponse addToMovie(String sessionNumber, String nameMovie) {
        Movie movie = movieService.getMovie(nameMovie);
        Session session = getSession(sessionNumber);
        session.setMovie(movie);
        Session save = sessionRepository.save(session);
//        SessionDTOResponse response = mapper.convertValue(save, SessionDTOResponse.class);
//        response.setMovieDTO(mapper.convertValue(movie, MovieDTO.class));
        SessionDTOResponse response = get(save.getSessionNumber());
        return response;
    }

    @Override
    public SessionDTOResponse addToCinema(String sessionNumber, String nameCinema) {
        Cinema cinema = cinemaService.getCinema(nameCinema);
        Session session = getSession(sessionNumber);
        session.setCinema(cinema);
        Session save = sessionRepository.save(session);
//        SessionDTOResponse response = mapper.convertValue(save, SessionDTOResponse.class);
//        response.setCinemaDTO(mapper.convertValue(cinema, CinemaDTO.class));
        SessionDTOResponse response = get(save.getSessionNumber());

        return response;
    }

    @Override
    public SessionDTOResponse addToHall(String sessionNumber, Integer numberHall) {
        Hall hall = hallService.getHall(numberHall);
        Session session = getSession(sessionNumber);
        session.setHall(hall);
        Session save = sessionRepository.save(session);
//        SessionDTOResponse response = mapper.convertValue(save, SessionDTOResponse.class);
//        response.setHallDTORequest(mapper.convertValue(hall, HallDTORequest.class));
        SessionDTOResponse response = get(save.getSessionNumber());
        return response;
    }

    @Override
    public List<SessionDTOResponse> getAllSession(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Session> pageResult = sessionRepository.findAll(pageRequest);

        List<SessionDTOResponse> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, SessionDTOResponse.class))
                .collect(Collectors.toList());

        return collect;
    }
}
