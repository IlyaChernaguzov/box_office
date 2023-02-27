package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.model.dto.MovieDTO;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.entity.Movie;
import com.example.demo.model.enums.HallStatus;
import com.example.demo.model.enums.MovieStatus;
import com.example.demo.model.repository.HallRepository;
import com.example.demo.model.repository.MovieRepository;
import com.example.demo.service.MovieService;
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
public class MovieServiceimpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ObjectMapper mapper;

    @Override
    public MovieDTO create(MovieDTO movieDTO) {
        movieRepository.findByNameMovie(movieDTO.getNameMovie()).ifPresent(
                c -> {throw new CustomException("Фильм с названием: " + movieDTO.getNameMovie() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Movie movie = mapper.convertValue(movieDTO, Movie.class);
        movie.setCreatedAt(LocalDateTime.now());
        Movie save = movieRepository.save(movie);
        return mapper.convertValue(save, MovieDTO.class);
    }

    @Override
    public MovieDTO update(MovieDTO movieDTO) {
        Movie movie = getMovie(movieDTO.getNameMovie());

        movie.setNameMovie(movieDTO.getNameMovie() == null ? movie.getNameMovie() : movieDTO.getNameMovie());
        movie.setDurationMovie(movieDTO.getDurationMovie() == null ? movie.getDurationMovie() : movieDTO.getDurationMovie());
        movie.setStartRental(movieDTO.getStartRental() == null ? movie.getStartRental() : movieDTO.getStartRental());
        movie.setEndRental(movieDTO.getEndRental() == null ? movie.getEndRental() : movieDTO.getEndRental());
        movie.setRatingMovie(movieDTO.getRatingMovie() == null ? movie.getRatingMovie() : movieDTO.getRatingMovie());
        movie.setGenreMovie(movieDTO.getGenreMovie() == null ? movie.getGenreMovie() : movieDTO.getGenreMovie());
        movie.setUpdatedAt(LocalDateTime.now());
        movie.setStatus(MovieStatus.UPDATED);
        Movie save = movieRepository.save(movie);
        return mapper.convertValue(save, MovieDTO.class);
    }

    @Override
    public MovieDTO get(String nameMovie) {

        Movie movie = getMovie(nameMovie);
        return mapper.convertValue(movie, MovieDTO.class);
    }

    @Override
    public void delete(String nameMovie) {

        Movie movie = getMovie(nameMovie);
        movie.setStatus(MovieStatus.DELETED);
        movie.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        movieRepository.save(movie);

    }

    @Override
    public Movie getMovie(String nameMovie) {
        return movieRepository.findByNameMovie(nameMovie)
                .orElseThrow(()-> new CustomException("Фильм с названием: " + nameMovie + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<MovieDTO> getAllMovie(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Movie> pageResult = movieRepository.findAll(pageRequest);

        List<MovieDTO> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, MovieDTO.class))
                .collect(Collectors.toList());

        return collect;
    }
}
