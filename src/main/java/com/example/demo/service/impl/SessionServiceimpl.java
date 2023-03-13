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
        sessionRepository
                .findSessionByHallAndStartSession(hallService.getHall(sessionDTORequest.getId()), sessionDTORequest.getStartSession())
                .ifPresent(
                        c -> {throw new CustomException("Такой сеанс уже существует", HttpStatus.BAD_REQUEST);
                });

//        sessionRepository.findBySessionNumber(sessionDTORequest.getSessionNumber()).ifPresent(
//                c -> {throw new CustomException("Сеанс под номером: " + sessionDTORequest.getSessionNumber() + " уже существует", HttpStatus.BAD_REQUEST);
//                }
//        );

//        Session session = new Session();
        Session session = mapper.convertValue(sessionDTORequest, Session.class);
        session.setHall(hallService.getHall(sessionDTORequest.getId()));
        session.setMovie(movieService.getMovie(sessionDTORequest.getNameMovie()));
        session.setCreatedAt(LocalDateTime.now());
        Session save = sessionRepository.save(session);
        return get(save.getIdSession());
    }

    @Override
    public SessionDTOResponse update(SessionDTORequest sessionDTORequest, Long idSession) {
        Session session = getSession(idSession);

//        session.setSessionNumber(sessionDTOUpdate.getSessionNumber() == null ? session.getSessionNumber() : sessionDTOUpdate.getSessionNumber());// ВОПРОС!
        session.setStartSession(sessionDTORequest.getStartSession() == null ? session.getStartSession() : sessionDTORequest.getStartSession());
        session.setPrice(sessionDTORequest.getPrice() == null ? session.getPrice() : sessionDTORequest.getPrice());
        session.setMovie(sessionDTORequest.getNameMovie() == null ? session.getMovie() : movieService.getMovie(sessionDTORequest.getNameMovie()));
//        session.setCinema(sessionDTOUpdate.getNameCinema() == null ? session.getCinema() : cinemaService.getCinema(sessionDTOUpdate.getNameCinema()));
        session.setHall(sessionDTORequest.getId() == null ? session.getHall() : hallService.getHall(sessionDTORequest.getId()));
        session.setUpdatedAt(LocalDateTime.now());
        session.setSessionStatus(SessionStatus.UPDATED);
        Session save = sessionRepository.save(session);

        return get(save.getIdSession());
    }

    @Override
    public SessionDTOResponse get(Long idSession) {
        Session session = getSession(idSession);
        MovieDTO movie = mapper.convertValue(session.getMovie(), MovieDTO.class);
        HallDTOResponse hall = mapper.convertValue(session.getHall(), HallDTOResponse.class);
        CinemaDTO cinema = mapper.convertValue(session.getHall().getCinema(), CinemaDTO.class);
        hall.setCinemaDTO(cinema);
        SessionDTOResponse result = mapper.convertValue(session, SessionDTOResponse.class);
        result.setMovieDTO(movie);
        result.setHallDTOResponse(hall);
        result.setIdSession(idSession);

        return result;
    }

    @Override
    public void delete(Long idSession) {

        Session session = getSession(idSession);

        session.setSessionStatus(SessionStatus.DELETED);
        session.setUpdatedAt(LocalDateTime.now());
//        carRepository.delete(car);// полное удаление
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
    public List<SessionDTOResponse> getAllSession(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Session> pageResult = sessionRepository.findAll(pageRequest);

        List<SessionDTOResponse> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, SessionDTOResponse.class))
                .collect(Collectors.toList());

        return collect;
    }
}
