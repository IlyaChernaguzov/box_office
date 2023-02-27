package com.example.demo.service;

import com.example.demo.model.dto.CarDTOResponse;
import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.dto.OrderDTO;
import com.example.demo.model.entity.Order;
import com.example.demo.model.entity.Place;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface OrderService {
    OrderDTO create(OrderDTO orderDTO);

    OrderDTO update(OrderDTO orderDTO);

    OrderDTO get(Long id);

    void delete(Long id);

    Order getOrder(Long id);

    OrderDTO addToUser (Long id, String email);

    OrderDTO addToPlace (Long id, Long idPlace);

    OrderDTO addToSession (Long id, Long idSession);

    List<OrderDTO> getAllOrder(Integer page, Integer perPage, String sort, Sort.Direction order);
}
