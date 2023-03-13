package com.example.demo.controllers;

import com.example.demo.model.dto.HallDTORequest;
import com.example.demo.model.dto.HallDTOResponse;
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
    @Operation(summary = "Создание зала")
    private ResponseEntity<HallDTOResponse> createHall(@RequestBody HallDTORequest hallDTORequest,
                                                       @RequestParam String nameCinema){
        return ResponseEntity.ok(hallService.create(hallDTORequest, nameCinema));

    }

    @PutMapping
    @Operation(summary = "Обновление данных зала")
    private ResponseEntity<HallDTOResponse> updateHall(@RequestBody HallDTORequest hallDTORequest,
                                                       @RequestParam String nameCinema,
                                                       @RequestParam Long id){
        return ResponseEntity.ok(hallService.update(hallDTORequest, nameCinema, id));

    }

    @GetMapping
    @Operation(summary = "Посмотреть зал")
    private ResponseEntity<HallDTOResponse> getHall(@RequestParam Long id){
        return ResponseEntity.ok(hallService.get(id));

    }

    @DeleteMapping
    @Operation(summary = "Удалить зал")
    private ResponseEntity<HttpStatus> deleteHall(@RequestParam Long id){
        hallService.delete(id);
        return ResponseEntity.ok().build();

    }

//    @PostMapping("/hallToCinema")
//    @Operation(summary = "Присваивание залу кинотеатр")
//    private ResponseEntity<HallDTOResponse> addToCinema(@RequestBody Integer numberHall, @RequestParam String nameCinema) {
//        return ResponseEntity.ok(hallService.addToCinema(numberHall, nameCinema));// обертка ответа со статусом ок
//
//    }

    @GetMapping("/all")
    @Operation(summary = "Сортировать залы")
    public List<HallDTOResponse> getAllHall(@RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                            @RequestParam(required = false, defaultValue = "numberHall") String sort,
                                            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return hallService.getAllHall(page, perPage, sort, order);

    }
}
