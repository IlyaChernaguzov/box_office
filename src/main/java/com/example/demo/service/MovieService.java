package com.example.demo.service;

import com.example.demo.model.dto.MovieDTO;
import com.example.demo.model.entity.Movie;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface MovieService {
    MovieDTO create(MovieDTO movieDTO);

    MovieDTO update(MovieDTO movieDTO);

    MovieDTO get(String nameMovie);

    void delete(String nameMovie);

    Movie getMovie(String nameMovie);

    List<MovieDTO> getAllMovie(Integer page, Integer perPage, String sort, Sort.Direction order);
}
