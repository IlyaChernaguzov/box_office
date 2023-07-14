package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.Booking;
import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.Rating;
import com.example.demo.model.repository.SessionRepository;
import com.example.demo.service.HallService;
import com.example.demo.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionServiceimplTest {

    @InjectMocks
    private SessionServiceimpl sessionService;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private MovieService movieService;
    @Mock
    private HallService hallService;
    @Spy
    private ObjectMapper mapper;

    @Test
    public void create() {
        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);

        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setCinema(cinema);

        HallDTO hallDTO = new HallDTO();
        hallDTO.setIdHall(hall.getIdHall());

        Movie movie = new Movie();
        movie.setIdMovie(1L);
//        movie.setNameMovie("IT");
//        movie.setDurationMovie(155);
//        movie.setStartRental(LocalDateTime.parse("2023-01-01T10:00:00"));
//        movie.setEndRental(LocalDateTime.parse("2023-01-01T10:00:00"));
//        movie.setRatingMovie(Rating.RARS_18);
//        movie.setGenreMovie(Genre.THRILLER);

        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setIdMovie(movie.getIdMovie());
//        movieDTO.setNameMovie("IT");
//        movieDTO.setDurationMovie(155);
//        movieDTO.setStartRental("2023-01-01T10:00:00");
//        movieDTO.setEndRental("2023-01-01T10:00:00");
//        movieDTO.setRatingMovie(Rating.RARS_18);
//        movieDTO.setGenreMovie(Genre.THRILLER);

        Session session = new Session();
        session.setIdSession(1L);
        session.setStartSession("10/00");
        session.setPrice(100);
        session.setHall(hall);
        session.setMovie(movie);
        session.setCinema(hall.getCinema());

        when(hallService.getHall(anyLong())).thenReturn(hall);
        when(movieService.getMovie(anyLong())).thenReturn(movie);
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionRepository.findSessionByIdSession(anyLong())).thenReturn(Optional.of(session));
        when(movieService.get(anyLong())).thenReturn(movieDTO);
        when(hallService.get(anyLong())).thenReturn(hallDTO);

//        when(sessionRepository.save(any(Session.class)))
//                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        SessionDTOCreate test = new SessionDTOCreate();
        test.setStartSession("10/00");
        test.setPrice(100);
        test.setIdHall(1L);
        test.setIdMovie(1L);

        SessionDTO res = sessionService.create(test);
        assertEquals(test.getPrice(), res.getPrice());
        assertEquals(test.getIdHall(), res.getHallDTO().getIdHall());
        assertNotNull(res);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionFindSession() {
        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);

        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setCinema(cinema);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        SessionDTOCreate test = new SessionDTOCreate();
        test.setStartSession("10/00");
        test.setPrice(100);
        test.setIdHall(1L);
        test.setIdMovie(1L);

        when(sessionRepository.findSessionByHallAndStartSession(any(Hall.class), anyString())).thenThrow(CustomException.class);
        sessionService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionStartSessionIsNull() {
        SessionDTOCreate test = new SessionDTOCreate();
        test.setStartSession("");
        test.setPrice(100);
        test.setIdHall(1L);
        test.setIdMovie(1L);

        sessionService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionPriceIsNull() {
        SessionDTOCreate test = new SessionDTOCreate();
        test.setStartSession("10/00");
        test.setPrice(null);
        test.setIdHall(1L);
        test.setIdMovie(1L);

        sessionService.create(test);
    }

    @Test
    public void update() {
        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);

        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setCinema(cinema);

        HallDTO hallDTO = new HallDTO();
        hallDTO.setIdHall(hall.getIdHall());
        hallDTO.setIdCinema(cinema.getIdCinema());

        Movie movie = new Movie();
        movie.setIdMovie(1L);

        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setIdMovie(movie.getIdMovie());

        Session session = new Session();
        session.setIdSession(1L);
        session.setStartSession("20/00");
        session.setPrice(200);
        session.setHall(hall);
        session.setMovie(movie);
        session.setCinema(hall.getCinema());

        when(hallService.getHall(anyLong())).thenReturn(hall);
        when(movieService.getMovie(anyLong())).thenReturn(movie);
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionRepository.findSessionByIdSession(anyLong())).thenReturn(Optional.of(session));
        when(movieService.get(anyLong())).thenReturn(movieDTO);
        when(hallService.get(anyLong())).thenReturn(hallDTO);

//        when(sessionRepository.save(any(Session.class)))
//                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        SessionDTOUpdate test = new SessionDTOUpdate();
        test.setIdSession(1L);
        test.setStartSession("10/00");
//        test.setPrice(100);
        test.setIdHall(1L);
        test.setIdMovie(1L);

        SessionDTO res = sessionService.update(test);
        assertEquals(session.getPrice(), res.getPrice());
        assertEquals(test.getIdHall(), res.getHallDTO().getIdHall());
        assertEquals(session.getHall().getCinema().getIdCinema(), res.getHallDTO().getIdCinema());
        assertNotNull(res);
    }

    @Test
    public void get() {
    }

    @Test
    public void delete() {
        Session test = new Session();
        test.setIdSession(1L);

        when(sessionRepository.findSessionByIdSession(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(sessionRepository.save(any(Session.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        sessionService.delete(test.getIdSession());
        verify(sessionRepository, Mockito.times(1)).save(test);// произошло ли одно взаимодействие с БД
    }

    @Test
    public void getSession() {
    }

    @Test
    public void getAllSession() {

        MovieDTO movieDTO1 = new MovieDTO();
        movieDTO1.setIdMovie(1L);

        MovieDTO movieDTO2 = new MovieDTO();
        movieDTO1.setIdMovie(2L);

        HallDTO hallDTO1 = new HallDTO();
        hallDTO1.setIdHall(1L);
        hallDTO1.setIdCinema(1L);

        HallDTO hallDTO2 = new HallDTO();
        hallDTO2.setIdHall(2L);
        hallDTO2.setIdCinema(2L);

        Integer page = 1;
        Integer perPage = 2;
        String sort = "idSession";
        Sort.Direction order = Sort.Direction.DESC;

        List<Session> sessions = getSessions();

        Page<Session> pageResult = mock(Page.class);

        when(sessionRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(sessions);

//        when(movieService.get(1L)).thenReturn(movieDTO1);
        when(hallService.get(1L)).thenReturn(hallDTO1);
        when(movieService.get(2L)).thenReturn(movieDTO2);
        when(hallService.get(2L)).thenReturn(hallDTO2);

        List<SessionDTO> res = sessionService.getAllSession(page, perPage, sort, order);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(sessions.get(0).getPrice(), res.get(0).getPrice());
        assertEquals(sessions.get(0).getHall().getCinema().getIdCinema(), res.get(0).getHallDTO().getIdCinema());
    }
    private List<Session> getSessions(){
        Session first = new Session();
        Session second = new Session();

        Cinema cinema1 = new Cinema();
        cinema1.setIdCinema(1L);

        Hall hall1 = new Hall();
        hall1.setIdHall(1L);
        hall1.setCinema(cinema1);

        HallDTO hallDTO1 = new HallDTO();
        hallDTO1.setIdHall(hall1.getIdHall());
        hallDTO1.setIdCinema(cinema1.getIdCinema());

        Movie movie1 = new Movie();
        movie1.setIdMovie(1L);

        MovieDTO movieDTO1 = new MovieDTO();
        movieDTO1.setIdMovie(movie1.getIdMovie());

        Cinema cinema2 = new Cinema();
        cinema2.setIdCinema(2L);

        Hall hall2 = new Hall();
        hall2.setIdHall(2L);
        hall2.setCinema(cinema2);

        HallDTO hallDTO2 = new HallDTO();
        hallDTO2.setIdHall(hall1.getIdHall());
        hallDTO2.setIdCinema(cinema1.getIdCinema());

        Movie movie2 = new Movie();
        movie1.setIdMovie(2L);

        MovieDTO movieDTO2 = new MovieDTO();
        movieDTO2.setIdMovie(movie2.getIdMovie());

        first.setIdSession(1L);
        first.setStartSession("10/00");
        first.setPrice(100);
        first.setHall(hall1);
        first.setMovie(movie1);
        first.setCinema(hall1.getCinema());

        second.setIdSession(1L);
        second.setStartSession("20/00");
        second.setPrice(200);
        second.setHall(hall2);
        second.setMovie(movie2);
        second.setCinema(hall2.getCinema());


        return List.of(first, second);
    }
}