package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.MovieStatus;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.repository.*;
import com.example.demo.service.OrderService;
import com.example.demo.service.PlaceService;
import com.example.demo.service.SessionService;
import com.example.demo.service.UserService;
import com.example.demo.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceimpl implements OrderService {

    private final OrderRepository orderRepository;
//    private final UserRepository userRepository;
//    private final PlaceRepository placeRepository;
//    private final SessionRepository sessionRepository;
    private final UserService userService;
    private final PlaceService placeService;
    private final SessionService sessionService;
    private final ObjectMapper mapper;

    @Override
    public OrderDTO create(OrderDTO orderDTO) {
        orderRepository.findById(orderDTO.getId()).ifPresent(
                c -> {throw new CustomException("Заказ под номером: " + orderDTO.getId() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        Order order = mapper.convertValue(orderDTO, Order.class);
        order.setCreatedAt(LocalDateTime.now());
        Order save = orderRepository.save(order);
        return mapper.convertValue(save, OrderDTO.class);
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        Order order = getOrder(orderDTO.getId());

        order.setId(orderDTO.getId() == null ? order.getId() : orderDTO.getId());
//        order.setUser(orderDTO.getUserDTO() == null ? order.getUser() : orderDTO.getUserDTO());
//        order.setPlace(orderDTO.getPlaceDTO() == null ? order.getPlace() : orderDTO.getPlaceDTO());
//        order.setSession(orderDTO.getSessionDTO() == null ? order.getSession() : orderDTO.getSessionDTO());// ВОПРОС!
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.UPDATED);
        Order save = orderRepository.save(order);

        return mapper.convertValue(save, OrderDTO.class);
    }

    @Override
    public OrderDTO get(Long id) {
        Order order = getOrder(id);
        UserDTO user = mapper.convertValue(order.getUser(), UserDTO.class);
        PlaceDTO place = mapper.convertValue(order.getPlace(), PlaceDTO.class);
        SessionDTO session = mapper.convertValue(order.getSession(), SessionDTO.class);
        OrderDTO result = mapper.convertValue(order, OrderDTO.class);
        result.setUserDTO(user);
        result.setPlaceDTO(place);
        result.setSessionDTO(session);// ВОПРОС!

        return result;
    }

    @Override
    public void delete(Long id) {

        Order order = getOrder(id);

        order.setOrderStatus(OrderStatus.DELETED);
        order.setUpdatedAt(LocalDateTime.now());
//        carRepository.delete(car);// полное удаление
        orderRepository.save(order);

    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(()-> new CustomException("Заказ под номером" + id + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public OrderDTO addToUser(Long id, String email) {

        User user = userService.getUser(email);
        Order order = getOrder(id);
        order.setUser(user);
        Order save = orderRepository.save(order);
        OrderDTO response = mapper.convertValue(save, OrderDTO.class);
        response.setUserDTO(mapper.convertValue(user, UserDTO.class));
        return response;
    }

    @Override
    public OrderDTO addToPlace(Long id, Long idPlace) {

        Place place = placeService.getPlace(idPlace);
        Order order = getOrder(id);
        order.setPlace((List<Place>) place);//ВОПРОС
        Order save = orderRepository.save(order);
        OrderDTO response = mapper.convertValue(save, OrderDTO.class);
        response.setPlaceDTO(mapper.convertValue(place, PlaceDTO.class));
        return response;
    }

    @Override
    public OrderDTO addToSession(Long id, Long idSession) {

        Session session = sessionService.getSession(idSession);
        Order order = getOrder(id);
        order.setSession(session);
        Order save = orderRepository.save(order);
        OrderDTO response = mapper.convertValue(save, OrderDTO.class);
        response.setSessionDTO(mapper.convertValue(session, SessionDTO.class));
        return response;
    }

    @Override
    public List<OrderDTO> getAllOrder(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Order> pageResult = orderRepository.findAll(pageRequest);

        List<OrderDTO> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, OrderDTO.class))
                .collect(Collectors.toList());

        return collect;
    }
}
