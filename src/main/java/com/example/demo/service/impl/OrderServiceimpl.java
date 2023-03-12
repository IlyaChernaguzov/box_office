package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.text.html.HTMLDocument;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceimpl implements OrderService {

    private final OrderRepository orderRepository;
//    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final SessionRepository sessionRepository;
    private final UserService userService;
    private final PlaceService placeService;
    private final SessionService sessionService;
    private final ObjectMapper mapper;

    @Override
    public OrderDTOResponse create(OrderDTO orderDTO) {

        List<Order> orders = orderRepository.findBySession(sessionService.getSession(orderDTO.getSessionNumber()))
                .stream()
                .filter(o -> Objects.equals(o.getPlace().getPlaceNumber(), orderDTO.getPlaceNumber()))
                .collect(Collectors.toList());

        if (!orders.isEmpty())
        {
            throw new CustomException("Эта бронь уже существует", HttpStatus.BAD_REQUEST);
        }


        Order order = new Order();
//        order.setUser(userService.getUser(orderDTORequest.getEmail()));
        order.setPlace(placeService.getPlace(orderDTO.getPlaceNumber()));
        order.setSession(sessionService.getSession(orderDTO.getSessionNumber()));
        order.setBooking(orderDTO.getBooking());
        order.setCreatedAt(LocalDateTime.now());
        Order save = orderRepository.save(order);
//        OrderDTOResponse response = get(save.getIdOrder());
        OrderDTOResponse response = get(save.getIdOrder());
//        mapper.convertValue(save, OrderDTOResponse.class);

        return response;
    }

    @Override
    public OrderDTOResponse update(OrderDTOUpdate orderDTOUpdate) {
        Order order = getOrder(orderDTOUpdate.getIdOrder());

//        order.setUser(orderDTOUpdate.getEmail() == null ? order.getUser() : userService.getUser(orderDTO.getEmail()));
        order.setPlace(orderDTOUpdate.getPlaceNumber() == null ? order.getPlace() : placeService.getPlace(orderDTOUpdate.getPlaceNumber()));
        order.setSession(orderDTOUpdate.getSessionNumber() == null ? order.getSession() : sessionService.getSession(orderDTOUpdate.getSessionNumber()));// ВОПРОС!
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.UPDATED);
        Order save = orderRepository.save(order);
        OrderDTOResponse response = get(save.getIdOrder());

        return response;
    }

    @Override
    public OrderDTOResponse get(Long idOrder) {
        Order order = getOrder(idOrder);
        UserDTO user = mapper.convertValue(order.getUser(), UserDTO.class);
        PlaceDTORequest place = mapper.convertValue(order.getPlace(), PlaceDTORequest.class);
        SessionDTORequest session = mapper.convertValue(order.getSession(), SessionDTORequest.class);
        OrderDTOResponse result = mapper.convertValue(order, OrderDTOResponse.class);
        result.setUserDTO(user);
        result.setPlaceDTORequest(place);
        result.setSessionDTORequest(session);// ВОПРОС!

        return result;
    }

    @Override
    public void delete(Long idOrder) {

        Order order = getOrder(idOrder);

        order.setOrderStatus(OrderStatus.DELETED);
        order.setUpdatedAt(LocalDateTime.now());
//        orderRepository.delete(order);// полное удаление
        orderRepository.save(order);

    }

    @Override
    public Order getOrder(Long idOrder) {
        return orderRepository.findById(idOrder)
                .orElseThrow(()-> new CustomException("Заказ под номером" + idOrder + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public OrderDTOResponse addToUser(Long idOrder, String email) {

        User user = userService.getUser(email);
        Order order = getOrder(idOrder);
        order.setUser(user);
        Order save = orderRepository.save(order);
        OrderDTOResponse response = mapper.convertValue(save, OrderDTOResponse.class);
        response.setUserDTO(mapper.convertValue(user, UserDTO.class));
        return response;
    }

    @Override
    public OrderDTOResponse addToPlace(Long idOrder, Integer placeNumber) {

        Place place = placeService.getPlace(placeNumber);
        Order order = getOrder(idOrder);
        order.setPlace(place);
        Order save = orderRepository.save(order);
        OrderDTOResponse response = mapper.convertValue(save, OrderDTOResponse.class);
        response.setPlaceDTORequest(mapper.convertValue(place, PlaceDTORequest.class));
        return response;
    }

    @Override
    public OrderDTOResponse addToSession(Long idOrder, String sessionNumber) {

        Session session = sessionService.getSession(sessionNumber);
        Order order = getOrder(idOrder);
        order.setSession(session);
        Order save = orderRepository.save(order);
        OrderDTOResponse response = mapper.convertValue(save, OrderDTOResponse.class);
        response.setSessionDTORequest(mapper.convertValue(session, SessionDTORequest.class));
        return response;
    }

    @Override
    public List<OrderDTOResponse> getAllOrder(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Order> pageResult = orderRepository.findAll(pageRequest);

        List<OrderDTOResponse> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, OrderDTOResponse.class))
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<OrderDTOResponse> getAllOrderBySession(String sessionNumber) {

        List<Order> orders = orderRepository.findBySession(sessionService.getSession(sessionNumber));

//       List<UserDTO> users = orders.stream()
//                .map(r -> mapper.convertValue(r.getUser(), UserDTO.class))
//                .collect(Collectors.toList());

        List<PlaceDTORequest> places = orders.stream()
                .map(r -> mapper.convertValue(r.getPlace(), PlaceDTORequest.class))
                .collect(Collectors.toList());

//        List<SessionDTORequest> sessions = orders.stream()
//                .map(r -> mapper.convertValue(r.getSession(), SessionDTORequest.class))
//                .collect(Collectors.toList());

        List<OrderDTOResponse> response = orders.stream()
                .map(r -> mapper.convertValue(r, OrderDTOResponse.class))
                .collect(Collectors.toList());

        for (int i = 0; i < response.size(); i++){
            response.get(i).setPlaceDTORequest(places.get(i));
        }
        return response;
    }

}
