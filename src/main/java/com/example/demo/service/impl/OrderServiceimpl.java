package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.Booking;
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
    private final PlaceService placeService;
    private final SessionService sessionService;
    private final ObjectMapper mapper;

    @Override
    public OrderDTO create(OrderDTOCreate orderDTOCreate) {

        Session session = sessionService.getSession(orderDTOCreate.getIdSession());
        Place place = placeService.getPlace(orderDTOCreate.getIdPlace());

        if (!Objects.equals(session.getHall().getIdHall(), place.getHall().getIdHall())){
            throw new CustomException("Место и сеанс находятся в разных залах", HttpStatus.BAD_REQUEST);
        }

        orderRepository
                .findOrderBySessionAndPlace(session, place)
                .ifPresent(
                        c -> {throw new CustomException("Такая бронь уже существует", HttpStatus.BAD_REQUEST);
                        });

//        List<Order> orders = orderRepository.findBySession(sessionService.getSession(orderDTO.getIdSession()))
//                .stream()
//                .filter(o -> Objects.equals(o.getPlace().getIdPlace(), orderDTO.getIdPlace()))
//                .collect(Collectors.toList());
//
//        if (!orders.isEmpty())
//        {
//            throw new CustomException("Эта бронь уже существует", HttpStatus.BAD_REQUEST);
//        }


        Order order = new Order();
        order.setPlace(placeService.getPlace(orderDTOCreate.getIdPlace()));
        order.setSession(sessionService.getSession(orderDTOCreate.getIdSession()));
        order.setBooking(Booking.FREE);
        order.setCreatedAt(LocalDateTime.now());
        return getOrderDTO(order);
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        Order order = getOrder(orderDTO.getIdOrder());

//        order.setUser(orderDTOUpdate.getEmail() == null ? order.getUser() : userService.getUser(orderDTO.getEmail()));
        order.setPlace(orderDTO.getIdPlace() == null ? order.getPlace() : placeService.getPlace(orderDTO.getIdPlace()));
        order.setSession(orderDTO.getIdSession() == null ? order.getSession() : sessionService.getSession(orderDTO.getIdSession()));

        if (!Objects.equals(order.getSession().getHall().getIdHall(), order.getPlace().getHall().getIdHall())){
            throw new CustomException("Место и сеанс находятся в разных залах", HttpStatus.BAD_REQUEST);
        }

        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.UPDATED);

        return getOrderDTO(order);
    }

    private OrderDTO getOrderDTO(Order order) {
        Order save = orderRepository.save(order);

        OrderDTO result = mapper.convertValue(save, OrderDTO.class);
        result.setIdOrder(save.getIdOrder());
        result.setIdPlace(save.getPlace().getIdPlace());
        result.setIdSession(save.getSession().getIdSession());
        return result;
    }

    @Override
    public OrderDTOResponse get(Long idOrder) {
        Order order = getOrder(idOrder);
        UserDTO user = mapper.convertValue(order.getUser(), UserDTO.class);
//        PlaceDTORequest place = mapper.convertValue(order.getPlace(), PlaceDTORequest.class);
//        SessionDTORequest session = mapper.convertValue(order.getSession(), SessionDTORequest.class);
        OrderDTOResponse result = mapper.convertValue(order, OrderDTOResponse.class);
        result.setIdOrder(order.getIdOrder());
        result.setNameMovie(order.getSession().getMovie().getNameMovie());
        result.setNameCinema(order.getPlace().getHall().getCinema().getNameCinema());
        result.setNumberHall(order.getPlace().getHall().getNumberHall());
        result.setRowNumber(order.getPlace().getRowNumber());
        result.setPlaceNumberInRow(order.getPlace().getPlaceNumberInRow());
        result.setStartSession(order.getSession().getStartSession());
        result.setPrice(order.getSession().getPrice());
        result.setBooking(order.getBooking());
        result.setUserDTO(user);

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
        return orderRepository.findByIdOrder(idOrder)
                .orElseThrow(()-> new CustomException("Заказ под номером" + idOrder + " не найден", HttpStatus.NOT_FOUND));
    }

//    @Override
//    public OrderDTOResponse addToUser(Long idOrder, String email) {
//
//        User user = userService.getUser(email);
//        Order order = getOrder(idOrder);
//        order.setUser(user);
//        Order save = orderRepository.save(order);
//        OrderDTOResponse response = mapper.convertValue(save, OrderDTOResponse.class);
//        response.setUserDTO(mapper.convertValue(user, UserDTO.class));
//        return response;
//    }
//
//    @Override
//    public OrderDTOResponse addToPlace(Long idOrder, Integer placeNumber) {
//
//        Place place = placeService.getPlace(placeNumber);
//        Order order = getOrder(idOrder);
//        order.setPlace(place);
//        Order save = orderRepository.save(order);
//        OrderDTOResponse response = mapper.convertValue(save, OrderDTOResponse.class);
//        response.setPlaceDTORequest(mapper.convertValue(place, PlaceDTORequest.class));
//        return response;
//    }
//
//    @Override
//    public OrderDTOResponse addToSession(Long idOrder, String sessionNumber) {
//
//        Session session = sessionService.getSession(sessionNumber);
//        Order order = getOrder(idOrder);
//        order.setSession(session);
//        Order save = orderRepository.save(order);
//        OrderDTOResponse response = mapper.convertValue(save, OrderDTOResponse.class);
//        response.setSessionDTORequest(mapper.convertValue(session, SessionDTORequest.class));
//        return response;
//    }

    @Override
    public List<OrderDTOResponse> getAllOrder(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Order> pageResult = orderRepository.findAll(pageRequest);

        List<OrderDTOResponse> collect = pageResult.getContent().stream()
                .map(h ->
                {
                    OrderDTOResponse response = new OrderDTOResponse();
                    response.setIdOrder (h
                            .getIdOrder()
                    );
                    response.setIdOrder(h.getIdOrder());
                    response.setNameMovie(h.getSession().getMovie().getNameMovie());
                    response.setNameCinema(h.getPlace().getHall().getCinema().getNameCinema());
                    response.setNumberHall(h.getPlace().getHall().getNumberHall());
                    response.setRowNumber(h.getPlace().getRowNumber());
                    response.setPlaceNumberInRow(h.getPlace().getPlaceNumberInRow());
                    response.setStartSession(h.getSession().getStartSession());
                    response.setPrice(h.getSession().getPrice());
                    response.setBooking(h.getBooking());
                    response.setUserDTO(mapper.convertValue(h.getUser(), UserDTO.class));

                    return response;
                })
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<OrderDTOResponse> getAllOrderBySession(Long idSession) {

        Session session = sessionService.getSession(idSession);

        List<Order> orders = orderRepository.findOrderBySession(session);

//        if (orders.isEmpty())
//        {
//            throw new CustomException("Такого сеанса нет", HttpStatus.BAD_REQUEST);
//        }

        return orders
                .stream()
                .map(h ->
                {
                    OrderDTOResponse response = new OrderDTOResponse();
                    response.setIdOrder (h
                            .getIdOrder()
                    );
                    response.setIdOrder(h.getIdOrder());
                    response.setNameMovie(h.getSession().getMovie().getNameMovie());
                    response.setNameCinema(h.getPlace().getHall().getCinema().getNameCinema());
                    response.setNumberHall(h.getPlace().getHall().getNumberHall());
                    response.setRowNumber(h.getPlace().getRowNumber());
                    response.setPlaceNumberInRow(h.getPlace().getPlaceNumberInRow());
                    response.setStartSession(h.getSession().getStartSession());
                    response.setPrice(h.getSession().getPrice());
                    response.setBooking(h.getBooking());
                    response.setUserDTO(mapper.convertValue(h.getUser(), UserDTO.class));

                    return response;
                })
                .collect(Collectors.toList());

//       List<UserDTO> users = orders.stream()
//                .map(r -> mapper.convertValue(r.getUser(), UserDTO.class))
//                .collect(Collectors.toList());

//        List<PlaceDTORequest> places = orders.stream()
//                .map(r -> mapper.convertValue(r.getPlace(), PlaceDTORequest.class))
//                .collect(Collectors.toList());
//
//        List<SessionDTORequest> sessions = orders.stream()
//                .map(r -> mapper.convertValue(r.getSession(), SessionDTORequest.class))
//                .collect(Collectors.toList());
//
//        List<OrderDTOResponse> response = orders.stream()
//                .map(r -> mapper.convertValue(r, OrderDTOResponse.class))
//                .collect(Collectors.toList());
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setPlaceDTORequest(places.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setSessionDTORequest(sessions.get(i));
//        }
//
//        return response;
    }

}
