package com.example.demo.controllers;

import com.example.demo.model.dto.MovieDTO;
import com.example.demo.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Tag(name = "Фильмы")
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Создание фильма")// описание в svagger ui
    private ResponseEntity<MovieDTO> createMovie(@RequestBody MovieDTO movieDTO){
        return ResponseEntity.ok(movieService.create(movieDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных фильма")
    private ResponseEntity<MovieDTO> updateMovie(@RequestBody MovieDTO movieDTO){
        return ResponseEntity.ok(movieService.update(movieDTO));

    }

    @GetMapping
    @Operation(summary = "Посмотреть фильм")
    private ResponseEntity<MovieDTO> getMovie(@RequestParam String nameMovie){
        return ResponseEntity.ok(movieService.get(nameMovie));

    }

    @DeleteMapping
    @Operation(summary = "Удалить фильм")
    private ResponseEntity<HttpStatus> deleteMovie(@RequestParam String nameMovie){
        movieService.delete(nameMovie);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать фильмы")
    public List<MovieDTO> getAllMovie(@RequestParam(required = false, defaultValue = "1") Integer page,
                                    @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                    @RequestParam(required = false, defaultValue = "nameMovie") String sort,
                                    @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return movieService.getAllMovie(page, perPage, sort, order);

    }
}
