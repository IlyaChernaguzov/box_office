package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.MovieDTO;
import com.example.demo.model.dto.MovieDTOCreate;
import com.example.demo.model.entity.Movie;
import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.MovieStatus;
import com.example.demo.model.enums.Rating;
import com.example.demo.model.repository.MovieRepository;
import com.example.demo.service.MovieService;
import com.example.demo.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
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
public class MovieServiceimpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ObjectMapper mapper;

    @Override
    public MovieDTO create(MovieDTOCreate movieDTOCreate) {
        if (StringUtils.isBlank(movieDTOCreate.getNameMovie())) {
            throw new CustomException("Поле NameMovie не может быть пустым", HttpStatus.BAD_REQUEST);
        }

        if (movieDTOCreate.getDurationMovie() == null) {
            throw new CustomException("Поле DurationMovie не может быть пустым", HttpStatus.BAD_REQUEST);
        }

        movieRepository.findByNameMovie(movieDTOCreate.getNameMovie()).ifPresent(
                c -> {throw new CustomException("Фильм с названием: " + movieDTOCreate.getNameMovie() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Movie movie = new Movie();
        movie.setNameMovie(movieDTOCreate.getNameMovie());
        movie.setDurationMovie(movieDTOCreate.getDurationMovie());
        movie.setStartRental(LocalDateTime.parse(movieDTOCreate.getStartRental()));
        movie.setEndRental(LocalDateTime.parse(movieDTOCreate.getEndRental()));
        movie.setRatingMovie(movieDTOCreate.getRatingMovie());
        movie.setGenreMovie(movieDTOCreate.getGenreMovie());
        movie.setCreatedAt(LocalDateTime.now());
        Movie save = movieRepository.save(movie);

        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        MovieDTO result = jsonMapper.convertValue(save, MovieDTO.class);
        result.setIdMovie(save.getIdMovie());
        return result;
//        return mapper.convertValue(save, MovieDTO.class);
//        return get(save.getIdMovie());
    }

    @Override
    public MovieDTO update(MovieDTO movieDTO) {
        Movie movie = getMovie(movieDTO.getIdMovie());

        movie.setNameMovie(StringUtils.isBlank(movieDTO.getNameMovie()) ? movie.getNameMovie() : movieDTO.getNameMovie());
        movie.setDurationMovie(movieDTO.getDurationMovie() == null ? movie.getDurationMovie() : movieDTO.getDurationMovie());
        movie.setStartRental(StringUtils.isBlank(movieDTO.getStartRental()) ? movie.getStartRental() : LocalDateTime.parse(movieDTO.getStartRental()));
        movie.setEndRental(StringUtils.isBlank(movieDTO.getEndRental()) ? movie.getEndRental() : LocalDateTime.parse(movieDTO.getEndRental()));
        movie.setRatingMovie(movieDTO.getRatingMovie() == null ? movie.getRatingMovie() : movieDTO.getRatingMovie());
        movie.setGenreMovie(movieDTO.getGenreMovie() == null ? movie.getGenreMovie() : movieDTO.getGenreMovie());
        movie.setUpdatedAt(LocalDateTime.now());
        movie.setMovieStatus(MovieStatus.UPDATED);
        Movie save = movieRepository.save(movie);
        return get(save.getIdMovie());
    }

    @Override
    public MovieDTO get(Long idMovie) {

        /*
        Ошибка Джексона: тип даты/времени Java 8 не поддерживается по умолчанию.
        Возникает, когда мы сериализуем объект Java или десериализуем JSON в POJO, и POJO содержит новые классы даты и времени Java 8, такие как LocalDate, LocalTime, LocalDateTime и т. Д.
         */
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());//Регистрация модуля в JsonMapper при использовании.JavaTimeModule

        Movie movie = getMovie(idMovie);
        MovieDTO result = jsonMapper.convertValue(movie, MovieDTO.class);
        result.setIdMovie(idMovie);
        return result;
    }

    @Override
    public void delete(Long idMovie) {

        Movie movie = getMovie(idMovie);
        movie.setMovieStatus(MovieStatus.DELETED);
        movie.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        movieRepository.save(movie);

    }

    @Override
    public Movie getMovie(Long idMovie) {
        return movieRepository.findMovieByIdMovie(idMovie)
                .orElseThrow(()-> new CustomException("Фильм с id: " + idMovie + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<MovieDTO> getAllMovie(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Movie> pageResult = movieRepository.findAll(pageRequest);

        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        List<MovieDTO> collect = pageResult.getContent().stream()
                .map(c -> jsonMapper.convertValue(c, MovieDTO.class))
                .collect(Collectors.toList());

        return collect;
    }
}
