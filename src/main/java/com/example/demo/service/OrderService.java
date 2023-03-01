package com.example.demo.service;

import com.example.demo.model.dto.OrderDTO;
import com.example.demo.model.entity.Order;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface OrderService {
    OrderDTO create(OrderDTO orderDTO);

    OrderDTO update(OrderDTO orderDTO);

    OrderDTO get(Long idOrder);

    void delete(Long idOrder);

    Order getOrder(Long idOrder);

    OrderDTO addToUser (Long idOrder, String email);

    OrderDTO addToPlace (Long idOrder, Long idPlace);

    OrderDTO addToSession (Long idOrder, Long idSession);

    List<OrderDTO> getAllOrder(Integer page, Integer perPage, String sort, Sort.Direction order);
}
