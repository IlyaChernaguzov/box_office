package com.example.demo.controllers;

import com.example.demo.model.dto.OrderDTO;
import com.example.demo.model.dto.PlaceDTO;
import com.example.demo.service.OrderService;
import com.example.demo.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
@Tag(name = "Места")
public class PlaceController {

    public final PlaceService placeService;

    @PostMapping
    @Operation(summary = "Создание места")// описание в swagger ui
    private ResponseEntity<PlaceDTO> createPlace(@RequestBody PlaceDTO placeDTO){
        return ResponseEntity.ok(placeService.create(placeDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление места")
    private ResponseEntity<PlaceDTO> updatePlace(@RequestBody PlaceDTO placeDTO){
        return ResponseEntity.ok(placeService.update(placeDTO));

    }

    @GetMapping
    @Operation(summary = "Посмотреть место")
    private ResponseEntity<PlaceDTO> getPlace(@RequestParam Long idPlace){//возвращает именно ответ CarDTOResponse
        return ResponseEntity.ok(placeService.get(idPlace));

    }

    @DeleteMapping
    @Operation(summary = "Удаление места")
    private ResponseEntity<HttpStatus> deletePlace(@RequestParam Long idPlace){
        placeService.delete(idPlace);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/placeToHall")
    @Operation(summary = "Присваивание месту зал")
    private ResponseEntity<PlaceDTO> addToHall(@RequestBody Long idPlace, @RequestParam Integer numberHall){
        return ResponseEntity.ok(placeService.addToHall(idPlace, numberHall));// обертка ответа со статусом ок

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать места")
    public List<PlaceDTO> getAllPlace(@RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                      @RequestParam(required = false, defaultValue = "idPlace") String sort,
                                      @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return placeService.getAllPlace(page, perPage, sort, order);

    }
}
