package com.example.demo.controllers;

import com.example.demo.model.dto.OrderDTO;
import com.example.demo.model.dto.SessionDTO;
import com.example.demo.service.OrderService;
import com.example.demo.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Сеансы")
public class SessionController {

    public final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Создание сеанса")// описание в svagger ui
    private ResponseEntity<SessionDTO> createOrder(@RequestBody SessionDTO sessionDTO){
        return ResponseEntity.ok(sessionService.create(sessionDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных сеанса")
    private ResponseEntity<SessionDTO> updateOrder(@RequestBody SessionDTO sessionDTO){
        return ResponseEntity.ok(sessionService.update(sessionDTO));

    }

    @GetMapping
    @Operation(summary = "Посмотреть сеанс")
    private ResponseEntity<SessionDTO> getOrder(@RequestParam Long idSession){//возвращает именно ответ CarDTOResponse
        return ResponseEntity.ok(sessionService.get(idSession));

    }

    @DeleteMapping
    @Operation(summary = "Удаление сеанса")
    private ResponseEntity<HttpStatus> deleteOrder(@RequestParam Long idSession){
        sessionService.delete(idSession);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/sessionToCinema")
    @Operation(summary = "Присваивание сеансу кинотеатр")
    private ResponseEntity<SessionDTO> addToCinema(@RequestBody Long idSession, @RequestParam String nameCinema){
        return ResponseEntity.ok(sessionService.addToCinema(idSession, nameCinema));// обертка ответа со статусом ок

    }

    @PostMapping("/sessionToMovie")
    @Operation(summary = "Присваивание сеансу фильма")
    private ResponseEntity<SessionDTO> addToMovie(@RequestBody Long idSession, @RequestParam  String nameMovie){
        return ResponseEntity.ok(sessionService.addToMovie(idSession, nameMovie));// обертка ответа со статусом ок

    }

    @PostMapping("/sessionToHall")
    @Operation(summary = "Присваивание сеансу зала")
    private ResponseEntity<SessionDTO> addToHall(@RequestBody Long idSession, @RequestParam Integer numberHall){
        return ResponseEntity.ok(sessionService.addToHall(idSession, numberHall));// обертка ответа со статусом ок

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать сеансы")
    public List<SessionDTO> getAllSession(@RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                      @RequestParam(required = false, defaultValue = "idSession") String sort,
                                      @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return sessionService.getAllSession(page, perPage, sort, order);

    }
}
