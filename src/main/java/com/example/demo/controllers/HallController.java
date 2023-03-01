package com.example.demo.controllers;

import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.service.CinemaService;
import com.example.demo.service.HallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/halls")
@RequiredArgsConstructor
@Tag(name = "Залы")
public class HallController {

    private final HallService hallService;

    @PostMapping
    @Operation(summary = "Создание зала")// описание в svagger ui
    private ResponseEntity<HallDTO> createHall(@RequestBody HallDTO hallDTO){
        return ResponseEntity.ok(hallService.create(hallDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных зала")
    private ResponseEntity<HallDTO> updateHall(@RequestBody HallDTO hallDTO){
        return ResponseEntity.ok(hallService.update(hallDTO));

    }

    @GetMapping
    @Operation(summary = "Посмотреть зал")
    private ResponseEntity<HallDTO> getHall(@RequestParam Integer numberHall){
        return ResponseEntity.ok(hallService.get(numberHall));

    }

    @DeleteMapping
    @Operation(summary = "Удалить зал")
    private ResponseEntity<HttpStatus> deleteHall(@RequestParam Integer numberHall){
        hallService.delete(numberHall);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать залы")
    public List<HallDTO> getAllHall(@RequestParam(required = false, defaultValue = "1") Integer page,
                                        @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                        @RequestParam(required = false, defaultValue = "numberHall") String sort,
                                        @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return hallService.getAllHall(page, perPage, sort, order);

    }
}
