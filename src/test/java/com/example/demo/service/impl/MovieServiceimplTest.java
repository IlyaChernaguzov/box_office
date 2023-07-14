package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.MovieDTO;
import com.example.demo.model.dto.MovieDTOCreate;
import com.example.demo.model.entity.Cinema;
import com.example.demo.model.entity.Movie;
import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.Rating;
import com.example.demo.model.repository.MovieRepository;
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
public class MovieServiceimplTest {

    @InjectMocks
    private MovieServiceimpl movieService;
    @Mock
    private MovieRepository movieRepository;
    @Spy
    private ObjectMapper mapper;

    @Test
    public void create() {
        MovieDTOCreate test = new MovieDTOCreate();
        test.setNameMovie("IT");
        test.setDurationMovie(155);
        test.setStartRental("2023-01-01T10:00:00");
        test.setEndRental("2023-01-01T10:00:00");
        test.setRatingMovie(Rating.RARS_18);
        test.setGenreMovie(Genre.THRILLER);

        when(movieRepository.save(any(Movie.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        MovieDTO res = movieService.create(test);
        assertEquals(test.getNameMovie(), res.getNameMovie());
        assertNotNull(res);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionFindMovie() {
        MovieDTOCreate test = new MovieDTOCreate();
        test.setNameMovie("IT");
        test.setDurationMovie(155);
        test.setStartRental("2023-01-01T10:00:00");
        test.setEndRental("2023-01-01T10:00:00");
        test.setRatingMovie(Rating.RARS_18);
        test.setGenreMovie(Genre.THRILLER);

        when(movieRepository.findByNameMovie(anyString())).thenThrow(CustomException.class);
        movieService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionNameMovieIsNull() {
        MovieDTOCreate test = new MovieDTOCreate();
        test.setNameMovie("");
        test.setDurationMovie(155);
        test.setStartRental("2023-01-01T10:00:00");
        test.setEndRental("2023-01-01T10:00:00");
        test.setRatingMovie(Rating.RARS_18);
        test.setGenreMovie(Genre.THRILLER);

        movieService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionDurationMovieIsNull() {
        MovieDTOCreate test = new MovieDTOCreate();
        test.setNameMovie("IT");
        test.setDurationMovie(null);
        test.setStartRental("2023-01-01T10:00:00");
        test.setEndRental("2023-01-01T10:00:00");
        test.setRatingMovie(Rating.RARS_18);
        test.setGenreMovie(Genre.THRILLER);

        movieService.create(test);
    }

    @Test
    public void update() {
        Movie test = new Movie();
        test.setIdMovie(1L);
        test.setNameMovie("IT");
        test.setDurationMovie(155);
        test.setStartRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        test.setEndRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        test.setRatingMovie(Rating.RARS_18);
        test.setGenreMovie(Genre.THRILLER);

        when(movieRepository.findMovieByIdMovie(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(movieRepository.save(any(Movie.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        MovieDTO testForUpdate = new MovieDTO();
        testForUpdate.setIdMovie(1L);
        testForUpdate.setDurationMovie(165);
        testForUpdate.setStartRental("2023-02-01T10:00:00");
        testForUpdate.setNameMovie(test.getNameMovie());

        MovieDTO res = movieService.update(testForUpdate);

        assertNotNull(res);// проверяем, что он не ноль
        assertSame(res.getNameMovie(),test.getNameMovie());//сверяем значение результата и заданные аргументы метода
        assertEquals(res.getDurationMovie(), testForUpdate.getDurationMovie());//проверяем, обновилось ли Halls
        assertEquals(res.getRatingMovie(),test.getRatingMovie());//так как в экземпляре для обновления мы не задавали это поле, оно должно остаться прежним, проверяем это.
        assertEquals(res.getStartRental(),testForUpdate.getStartRental());

    }

    @Test
    public void get() {
        Movie test = new Movie();
        test.setIdMovie(1L);
        test.setNameMovie("IT");
        test.setDurationMovie(155);
        test.setStartRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        test.setEndRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        test.setRatingMovie(Rating.RARS_18);
        test.setGenreMovie(Genre.THRILLER);

        when(movieRepository.findMovieByIdMovie(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional

        MovieDTO res = movieService.get(test.getIdMovie());

        assertNotNull(res);
        assertEquals(res.getDurationMovie(),test.getDurationMovie());
    }

    @Test
    public void delete() {
        Movie test = new Movie();
        test.setIdMovie(1L);
        test.setNameMovie("IT");
        test.setDurationMovie(155);
        test.setStartRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        test.setEndRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        test.setRatingMovie(Rating.RARS_18);
        test.setGenreMovie(Genre.THRILLER);

        when(movieRepository.findMovieByIdMovie(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(movieRepository.save(any(Movie.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        movieService.delete(test.getIdMovie());
        verify(movieRepository, Mockito.times(1)).save(test);// произошло ли одно взаимодействие с БД

    }

    @Test
    public void getMovie() {
    }

    @Test
    public void getAllMovie() {
        Integer page = 1;
        Integer perPage = 2;
        String sort = "nameMovie";
        Sort.Direction order = Sort.Direction.DESC;

        List<Movie> movies = getMovies();

        Page<Movie> pageResult = mock(Page.class);

        when(movieRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(movies);

        List<MovieDTO> res = movieService.getAllMovie(page, perPage, sort, order);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(movies.get(0).getNameMovie(), res.get(0).getNameMovie());
    }
    private List<Movie> getMovies(){
        Movie first = new Movie();
        Movie second = new Movie();

        first.setIdMovie(1L);
        first.setNameMovie("IT");
        first.setDurationMovie(155);
        first.setStartRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        first.setEndRental(LocalDateTime.parse("2023-01-01T10:00:00"));
        first.setRatingMovie(Rating.RARS_18);
        first.setGenreMovie(Genre.THRILLER);

        first.setIdMovie(2L);
        first.setNameMovie("Seven");
        first.setDurationMovie(130);
        first.setStartRental(LocalDateTime.parse("2023-02-01T10:00:00"));
        first.setEndRental(LocalDateTime.parse("2023-02-01T10:00:00"));
        first.setRatingMovie(Rating.RARS_18);
        first.setGenreMovie(Genre.THRILLER);

        return List.of(first, second);
    }
}