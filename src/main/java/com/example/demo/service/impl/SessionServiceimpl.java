package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.SessionStatus;
import com.example.demo.model.repository.CinemaRepository;
import com.example.demo.model.repository.HallRepository;
import com.example.demo.model.repository.MovieRepository;
import com.example.demo.model.repository.SessionRepository;
import com.example.demo.service.CinemaService;
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
    private final HallServiceimpl hallServiceimpl;
    private final ObjectMapper mapper;

    @Override
    public SessionDTO create(SessionDTO sessionDTO) {
        sessionRepository.findById(sessionDTO.getIdSession()).ifPresent(
                c -> {throw new CustomException("Сеанс под номером: " + sessionDTO.getIdSession() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Session session = mapper.convertValue(sessionDTO, Session.class);
        session.setCreatedAt(LocalDateTime.now());
        Session save = sessionRepository.save(session);
        return mapper.convertValue(save, SessionDTO.class);
    }

    @Override
    public SessionDTO update(SessionDTO sessionDTO) {
        Session session = getSession(sessionDTO.getIdSession());

        session.setIdSession(sessionDTO.getIdSession() == null ? session.getIdSession() : sessionDTO.getIdSession());// ВОПРОС!
        session.setStartSession(sessionDTO.getStartSession() == null ? session.getStartSession() : sessionDTO.getStartSession());
        session.setPrice(sessionDTO.getPrice() == null ? session.getPrice() : sessionDTO.getPrice());
//        session.setMovie(sessionDTO.getMovieDTO() == null ? session.getMovie() : sessionDTO.getMovieDTO());
//        session.setCinema(sessionDTO.getCinemaDTO() == null ? session.getCinema() : sessionDTO.getCinemaDTO());
//        session.setHall(sessionDTO.getHallDTO() == null ? session.getHall() : sessionDTO.getHallDTO());// ВОПРОС!
        session.setUpdatedAt(LocalDateTime.now());
        session.setSessionStatus(SessionStatus.UPDATED);
        Session save = sessionRepository.save(session);

        return mapper.convertValue(save, SessionDTO.class);
    }

    @Override
    public SessionDTO get(Long idSession) {
        Session session = getSession(idSession);
        MovieDTO movie = mapper.convertValue(session.getMovie(), MovieDTO.class);
        CinemaDTO cinema = mapper.convertValue(session.getCinema(), CinemaDTO.class);
        HallDTO hall = mapper.convertValue(session.getHall(), HallDTO.class);
        SessionDTO result = mapper.convertValue(session, SessionDTO.class);
        result.setMovieDTO(movie);
        result.setCinemaDTO(cinema);
        result.setHallDTO(hall);// ВОПРОС!

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
        return sessionRepository.findById(idSession)
                .orElseThrow(()-> new CustomException("Сеанс под номером" + idSession + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public SessionDTO addToMovie(Long idSession, String nameMovie) {
        Movie movie = movieService.getMovie(nameMovie);
        Session session = getSession(idSession);
        session.setMovie(movie);
        Session save = sessionRepository.save(session);
        SessionDTO response = mapper.convertValue(save, SessionDTO.class);
        response.setMovieDTO(mapper.convertValue(movie, MovieDTO.class));
        return response;
    }

    @Override
    public SessionDTO addToCinema(Long idSession, String nameCinema) {
        Cinema cinema = cinemaService.getCinema(nameCinema);
        Session session = getSession(idSession);
        session.setCinema(cinema);
        Session save = sessionRepository.save(session);
        SessionDTO response = mapper.convertValue(save, SessionDTO.class);
        response.setCinemaDTO(mapper.convertValue(cinema, CinemaDTO.class));
        return response;
    }

    @Override
    public SessionDTO addToHall(Long idSession, Integer numberHall) {
        Hall hall = hallServiceimpl.getHall(numberHall);
        Session session = getSession(idSession);
        session.setHall(hall);
        Session save = sessionRepository.save(session);
        SessionDTO response = mapper.convertValue(save, SessionDTO.class);
        response.setHallDTO(mapper.convertValue(hall, HallDTO.class));
        return response;
    }

    @Override
    public List<SessionDTO> getAllSession(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Session> pageResult = sessionRepository.findAll(pageRequest);

        List<SessionDTO> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, SessionDTO.class))
                .collect(Collectors.toList());

        return collect;
    }
}
