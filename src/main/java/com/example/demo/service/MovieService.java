package com.example.demo.service;

import com.example.demo.model.dto.MovieDTO;
import com.example.demo.model.dto.MovieDTOCreate;
import com.example.demo.model.entity.Movie;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface MovieService {
    MovieDTO create(MovieDTOCreate movieDTOCreate);

    MovieDTO update(MovieDTO movieDTO);

    MovieDTO get(Long idMovie);

    void delete(Long idMovie);

    Movie getMovie(Long idMovie);

    List<MovieDTO> getAllMovie(Integer page, Integer perPage, String sort, Sort.Direction order);
}
