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
import org.apache.commons.lang3.StringUtils;
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
    private final MovieService movieService;
    private final HallService hallService;
    private final ObjectMapper mapper;

    @Override
    public SessionDTO create(SessionDTOCreate sessionDTOCreate) {

        if (StringUtils.isBlank(sessionDTOCreate.getStartSession())) {
            throw new CustomException("Поле StartSession не может быть пустым", HttpStatus.BAD_REQUEST);
        }

        if (sessionDTOCreate.getPrice() == null) {
            throw new CustomException("Поле Price не может быть пустым", HttpStatus.BAD_REQUEST);
        }

        Hall hall = hallService.getHall(sessionDTOCreate.getIdHall());
        Movie movie = movieService.getMovie(sessionDTOCreate.getIdMovie());

        sessionRepository
                .findSessionByHallAndStartSession(hall, sessionDTOCreate.getStartSession())
                .ifPresent(
                        c -> {throw new CustomException("Такой сеанс уже существует", HttpStatus.BAD_REQUEST);
                });

//        sessionRepository.findBySessionNumber(sessionDTORequest.getSessionNumber()).ifPresent(
//                c -> {throw new CustomException("Сеанс под номером: " + sessionDTORequest.getSessionNumber() + " уже существует", HttpStatus.BAD_REQUEST);
//                }
//        );

//        Session session = new Session();
        Session session = new Session();
        session.setStartSession(sessionDTOCreate.getStartSession());
        session.setPrice(sessionDTOCreate.getPrice());
        session.setHall(hall);
        session.setMovie(movie);
        session.setCinema(hall.getCinema());
        session.setCreatedAt(LocalDateTime.now());
        Session save = sessionRepository.save(session);
        return get(save.getIdSession());
    }

    @Override
    public SessionDTO update(SessionDTOUpdate sessionDTOUpdate) {
        Session session = getSession(sessionDTOUpdate.getIdSession());

//        session.setSessionNumber(sessionDTOUpdate.getSessionNumber() == null ? session.getSessionNumber() : sessionDTOUpdate.getSessionNumber());// ВОПРОС!
        session.setStartSession(StringUtils.isBlank(sessionDTOUpdate.getStartSession()) ? session.getStartSession() : sessionDTOUpdate.getStartSession());
        session.setPrice(sessionDTOUpdate.getPrice() == null ? session.getPrice() : sessionDTOUpdate.getPrice());
        session.setMovie(sessionDTOUpdate.getIdMovie() == null ? session.getMovie() : movieService.getMovie(sessionDTOUpdate.getIdMovie()));
//        session.setCinema(sessionDTOUpdate.getNameCinema() == null ? session.getCinema() : cinemaService.getCinema(sessionDTOUpdate.getNameCinema()));
        session.setHall(sessionDTOUpdate.getIdHall() == null ? session.getHall() : hallService.getHall(sessionDTOUpdate.getIdHall()));
        session.setCinema(sessionDTOUpdate.getIdHall() == null ? session.getCinema() : hallService.getHall(sessionDTOUpdate.getIdHall()).getCinema());
        session.setUpdatedAt(LocalDateTime.now());
        session.setSessionStatus(SessionStatus.UPDATED);
        Session save = sessionRepository.save(session);

        return get(save.getIdSession());
    }

    @Override
    public SessionDTO get(Long idSession) {
        Session session = getSession(idSession);

        MovieDTO movie = movieService.get(session.getMovie().getIdMovie());
        HallDTO hall = hallService.get(session.getHall().getIdHall());
//        CinemaDTO cinema = mapper.convertValue(session.getHall().getCinema(), CinemaDTO.class);
//        hall.setCinemaDTO(cinema);
        SessionDTO result = mapper.convertValue(session, SessionDTO.class);
        result.setMovieDTO(movie);
        result.setHallDTO(hall);
        result.setIdSession(idSession);

        return result;
    }

    @Override
    public void delete(Long idSession) {

        Session session = getSession(idSession);

        session.setSessionStatus(SessionStatus.DELETED);
        session.setUpdatedAt(LocalDateTime.now());
        //sessionRepository.delete(session);// полное удаление
        sessionRepository.save(session);

    }

    @Override
    public Session getSession(Long idSession) {
        return sessionRepository.findSessionByIdSession(idSession)
                .orElseThrow(()-> new CustomException("Сеанс под номером " + idSession + " не найден", HttpStatus.NOT_FOUND));
    }
//
//    @Override
//    public SessionDTOResponse addToMovie(String sessionNumber, String nameMovie) {
//        Movie movie = movieService.getMovie(nameMovie);
//        Session session = getSession(sessionNumber);
//        session.setMovie(movie);
//        Session save = sessionRepository.save(session);
////        SessionDTOResponse response = mapper.convertValue(save, SessionDTOResponse.class);
////        response.setMovieDTO(mapper.convertValue(movie, MovieDTO.class));
//        SessionDTOResponse response = get(save.getSessionNumber());
//        return response;
//    }
//
//    @Override
//    public SessionDTOResponse addToCinema(String sessionNumber, String nameCinema) {
//        Cinema cinema = cinemaService.getCinema(nameCinema);
//        Session session = getSession(sessionNumber);
//        session.setCinema(cinema);
//        Session save = sessionRepository.save(session);
////        SessionDTOResponse response = mapper.convertValue(save, SessionDTOResponse.class);
////        response.setCinemaDTO(mapper.convertValue(cinema, CinemaDTO.class));
//        SessionDTOResponse response = get(save.getSessionNumber());
//
//        return response;
//    }
//
//    @Override
//    public SessionDTOResponse addToHall(String sessionNumber, Integer numberHall) {
//        Hall hall = hallService.getHall(numberHall);
//        Session session = getSession(sessionNumber);
//        session.setHall(hall);
//        Session save = sessionRepository.save(session);
////        SessionDTOResponse response = mapper.convertValue(save, SessionDTOResponse.class);
////        response.setHallDTORequest(mapper.convertValue(hall, HallDTORequest.class));
//        SessionDTOResponse response = get(save.getSessionNumber());
//        return response;
//    }

    @Override
    public List<SessionDTO> getAllSession(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Session> pageResult = sessionRepository.findAll(pageRequest);

        List<SessionDTO> collect = pageResult.getContent().stream()
                .map(c ->
                {
                    MovieDTO movie = movieService.get(c.getMovie().getIdMovie());
                    HallDTO hall = hallService.get(c.getHall().getIdHall());

                    SessionDTO response = mapper.convertValue(c, SessionDTO.class);
                    response.setMovieDTO(movie);
                    response.setHallDTO(hall);
                    response.setIdSession(c.getIdSession());

                    return response;
                })
                .collect(Collectors.toList());

        return collect;
    }
}
