package com.example.demo.controllers;

import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.service.CinemaService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создание пользователя")// описание в svagger ui
    private ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.create(userDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных пользователя")
    private ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.update(userDTO));

    }

    @GetMapping
    @Operation(summary = "Посмотреть пользователя")
    private ResponseEntity<UserDTO> getUser(@RequestParam String email){
        return ResponseEntity.ok(userService.get(email));

    }

    @DeleteMapping
    @Operation(summary = "Удалить пользователя")
    private ResponseEntity<HttpStatus> deleteUser(@RequestParam String email){
        userService.delete(email);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать пользователей")
    public List<UserDTO> getAllUser(@RequestParam(required = false, defaultValue = "1") Integer page,
                                        @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                        @RequestParam(required = false, defaultValue = "email") String sort,
                                        @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return userService.getAllUser(page, perPage, sort, order);

    }
}
