package com.example.demo.controllers;

import com.example.demo.model.dto.*;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы")
public class OrderController {

    public final OrderService orderService;

    @PostMapping
    @Operation(summary = "Создание заказа")// описание в svagger ui
    private ResponseEntity<OrderDTOResponse> createOrder(@RequestBody OrderDTO orderDTO){
        return ResponseEntity.ok(orderService.create(orderDTO));// обертка ответа со статусом ок

    }

    @PutMapping
    @Operation(summary = "Обновление данных заказа")
    private ResponseEntity<OrderDTOResponse> updateOrder(@RequestBody OrderDTOUpdate orderDTOUpdate){
        return ResponseEntity.ok(orderService.update(orderDTOUpdate));

    }

    @GetMapping
    @Operation(summary = "Посмотреть тзаказ")
    private ResponseEntity<OrderDTOResponse> getOrder(@RequestParam Long idOrder){//возвращает именно ответ CarDTOResponse
        return ResponseEntity.ok(orderService.get(idOrder));

    }

    @DeleteMapping
    @Operation(summary = "Удаление заказ")
    private ResponseEntity<HttpStatus> deleteOrder(@RequestParam Long idOrder){
        orderService.delete(idOrder);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/orderToUser")
    @Operation(summary = "Присваивание заказу пользователя")
    private ResponseEntity<OrderDTOResponse> addToUser(@RequestBody Long idOrder, @RequestParam String email){
        return ResponseEntity.ok(orderService.addToUser(idOrder, email));// обертка ответа со статусом ок

    }

    @PostMapping("/orderToPlace")
    @Operation(summary = "Присваивание заказу места")
    private ResponseEntity<OrderDTOResponse> addToPlace(@RequestBody Long idOrder, @RequestParam Integer placeNumber){
        return ResponseEntity.ok(orderService.addToPlace(idOrder, placeNumber));// обертка ответа со статусом ок

    }

    @PostMapping("/orderToSession")
    @Operation(summary = "Присваивание заказу сеанса")
    private ResponseEntity<OrderDTOResponse> addToSession(@RequestBody Long idOrder, @RequestParam String sessionNumber){
        return ResponseEntity.ok(orderService.addToSession(idOrder, sessionNumber));// обертка ответа со статусом ок

    }

    @GetMapping("/all")// пагинация и сортировка
    @Operation(summary = "Сортировать заказы")
    public List<OrderDTOResponse> getAllOrder(@RequestParam(required = false, defaultValue = "1") Integer page,
                                              @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                              @RequestParam(required = false, defaultValue = "idOrder") String sort,
                                              @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order){
        return orderService.getAllOrder(page, perPage, sort, order);

    }

    @GetMapping("/allOrderBySession")// пагинация и сортировка
    @Operation(summary = "Сортировать заказы по сеансу")
    public List<OrderDTOResponse> getOrderBySession(@RequestParam String sessionNumber){
        return orderService.getAllOrderBySession(sessionNumber);

    }
}
