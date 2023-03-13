package com.example.demo.service;

import com.example.demo.model.dto.*;
import com.example.demo.model.entity.Order;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface OrderService {
    OrderDTOResponse create(OrderDTO orderDTO);

    OrderDTOResponse update(OrderDTO orderDTO, Long idOrder);

    OrderDTOResponse get(Long idOrder);

    void delete(Long idOrder);

    Order getOrder(Long idOrder);

//    OrderDTOResponse addToUser (Long idOrder, String email);
//
//    OrderDTOResponse addToPlace (Long idOrder, Integer placeNumber);
//
//    OrderDTOResponse addToSession (Long idOrder, String sessionNumber);

    List<OrderDTOResponse> getAllOrder(Integer page, Integer perPage, String sort, Sort.Direction order);

    List<OrderDTOResponse> getAllOrderBySession (Long idSession);
}
