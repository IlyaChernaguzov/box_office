package com.example.demo.controllers;

import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cinemas")
@RequiredArgsConstructor
@Tag(name = "Кинотеатры")
public class CinemaController {

    private final CinemaService cinemaService;

    @PostMapping
    @Operation(summary = "Создание кинотеатра")// описание в svagger ui
    private ResponseEntity<CinemaDTO> createCinema(@RequestBody CinemaDTO cinemaDTO){
        return ResponseEntity.ok(cinemaService.create(cinemaDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных кинотеатра")
    private ResponseEntity<CinemaDTO> updateCinema(@RequestBody CinemaDTO cinemaDTO){
        return ResponseEntity.ok(cinemaService.update(cinemaDTO));

    }

    @GetMapping
    @Operation(summary = "Посмотреть кинотеатр")
    private ResponseEntity<CinemaDTO> getCinema(@RequestParam String nameCinema){
        return ResponseEntity.ok(cinemaService.get(nameCinema));

    }

    @DeleteMapping
    @Operation(summary = "Удалить кинотеатр")
    private ResponseEntity<HttpStatus> deleteCinema(@RequestParam String nameCinema){
        cinemaService.delete(nameCinema);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать кинотеатры")
    public List<CinemaDTO> getAllCinema(@RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                         @RequestParam(required = false, defaultValue = "nameCinema") String sort,
                                         @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return cinemaService.getAllCinema(page, perPage, sort, order);

    }
}
