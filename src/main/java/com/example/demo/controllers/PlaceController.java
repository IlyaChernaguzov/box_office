package com.example.demo.controllers;

import com.example.demo.model.dto.PlaceDTORequest;
import com.example.demo.model.dto.PlaceDTOResponse;
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
    private ResponseEntity<PlaceDTOResponse> createPlace(@RequestBody PlaceDTORequest placeDTORequest){
        return ResponseEntity.ok(placeService.create(placeDTORequest));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление места")
    private ResponseEntity<PlaceDTOResponse> updatePlace(@RequestBody PlaceDTORequest placeDTORequest){
        return ResponseEntity.ok(placeService.update(placeDTORequest));

    }

    @GetMapping
    @Operation(summary = "Посмотреть место")
    private ResponseEntity<PlaceDTOResponse> getPlace(@RequestParam Integer placeNumber){//возвращает именно ответ CarDTOResponse
        return ResponseEntity.ok(placeService.get(placeNumber));

    }

    @DeleteMapping
    @Operation(summary = "Удаление места")
    private ResponseEntity<HttpStatus> deletePlace(@RequestParam Integer placeNumber){
        placeService.delete(placeNumber);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/placeToHall")
    @Operation(summary = "Присваивание месту зал")
    private ResponseEntity<PlaceDTOResponse> addToHall(@RequestBody Integer placeNumber, @RequestParam Integer numberHall){
        return ResponseEntity.ok(placeService.addToHall(placeNumber, numberHall));// обертка ответа со статусом ок

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать места")
    public List<PlaceDTORequest> getAllPlace(@RequestParam(required = false, defaultValue = "1") Integer page,
                                             @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                             @RequestParam(required = false, defaultValue = "idPlace") String sort,
                                             @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return placeService.getAllPlace(page, perPage, sort, order);

    }

    @GetMapping("/allPlaceByHall")// пагинация и сортировка
    @Operation(summary = "Сортировать места по холлу")
    public List<PlaceDTORequest> getAllPlaceByHall(@RequestParam Integer numberHall){
        return placeService.getAllPlaceByHall(numberHall);

    }
}
