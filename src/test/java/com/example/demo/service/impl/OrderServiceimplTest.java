package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.OrderDTO;
import com.example.demo.model.dto.OrderDTOCreate;
import com.example.demo.model.dto.OrderDTOResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.Booking;
import com.example.demo.model.repository.OrderRepository;
import com.example.demo.service.HallService;
import com.example.demo.service.PlaceService;
import com.example.demo.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceimplTest {

    @InjectMocks
    private OrderServiceimpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PlaceService placeService;
    @Mock
    private SessionService sessionService;
    @Spy
    private ObjectMapper mapper;

    @Test
    public void create() {
        Hall hall = new Hall();
        hall.setIdHall(1L);

        Place place = new Place();
        place.setIdPlace(1L);
        place.setHall(hall);

        Session session = new Session();
        session.setIdSession(1L);
        session.setHall(hall);

        when(sessionService.getSession(anyLong())).thenReturn(session);
        when(placeService.getPlace(anyLong())).thenReturn(place);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        OrderDTOCreate test = new OrderDTOCreate();
        test.setIdPlace(1L);
        test.setIdSession(1L);

        OrderDTO res = orderService.create(test);
        assertEquals(test.getIdPlace(), res.getIdPlace());
        assertEquals(test.getIdSession(), res.getIdSession());
        assertNotNull(res);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionFindOrder() {
        OrderDTOCreate test = new OrderDTOCreate();
        test.setIdPlace(1L);
        test.setIdSession(1L);

        Hall hall = new Hall();
        hall.setIdHall(1L);

        Place place = new Place();
        place.setIdPlace(1L);
        place.setHall(hall);

        Session session = new Session();
        session.setIdSession(1L);
        session.setHall(hall);

        when(sessionService.getSession(anyLong())).thenReturn(session);
        when(placeService.getPlace(anyLong())).thenReturn(place);

        when(orderRepository.findOrderBySessionAndPlace(any(Session.class), any(Place.class))).thenThrow(CustomException.class);
        orderService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionDifferentHall() {
        Hall hall1 = new Hall();
        hall1.setIdHall(1L);
        hall1.setNumberHall(1);

        Hall hall2 = new Hall();
        hall2.setIdHall(2L);
        hall1.setNumberHall(2);

        OrderDTOCreate test = new OrderDTOCreate();
        test.setIdPlace(1L);
        test.setIdSession(1L);

        Place place = new Place();
        place.setIdPlace(1L);
        place.setHall(hall1);

        Session session = new Session();
        session.setIdSession(1L);
        session.setHall(hall2);

        when(sessionService.getSession(anyLong())).thenReturn(session);
        when(placeService.getPlace(anyLong())).thenReturn(place);

        orderService.create(test);
    }

    @Test
    public void update() {
        Hall hall = new Hall();
        hall.setIdHall(1L);

        Place place = new Place();
        place.setIdPlace(1L);
        place.setHall(hall);

        Session session = new Session();
        session.setIdSession(1L);
        session.setHall(hall);

        Order test = new Order();
        test.setIdOrder(1L);
        test.setPlace(place);
        test.setSession(session);

//        when(placeService.getPlace(anyLong())).thenReturn(place);
        when(sessionService.getSession(anyLong())).thenReturn(session);

        when(orderRepository.findByIdOrder(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        OrderDTO testForUpdate = new OrderDTO();
        testForUpdate.setIdOrder(1L);
        testForUpdate.setIdSession(1L);
        //testForUpdate.setIdPlace(1L);

        OrderDTO res = orderService.update(testForUpdate);

        assertNotNull(res);// проверяем, что он не ноль
        assertEquals(res.getIdSession(), testForUpdate.getIdSession());
        assertEquals(res.getIdPlace(), test.getPlace().getIdPlace());
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void update_exceptionDifferentHall() {
        Hall hall1 = new Hall();
        hall1.setIdHall(1L);
        hall1.setNumberHall(1);

        Hall hall2 = new Hall();
        hall2.setIdHall(2L);
        hall1.setNumberHall(2);

        OrderDTO test = new OrderDTO();
        test.setIdPlace(1L);
        test.setIdSession(1L);

        Place place = new Place();
        place.setIdPlace(1L);
        place.setHall(hall1);

        Session session = new Session();
        session.setIdSession(1L);
        session.setHall(hall2);

        orderService.update(test);
    }

    @Test
    public void get() {
        User user = new User();
        user.setName("Ilya");
        user.setEmail("Ilya@mail.ru");

        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);
        cinema.setHalls(10);
        cinema.setNameCinema("Big");

        Movie movie = new Movie();
        movie.setIdMovie(1L);
        movie.setNameMovie("IT");

        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(10);
        hall.setPlaces(10);
        hall.setRows(10);
        hall.setCinema(cinema);

        Place place = new Place();
        place.setIdPlace(1L);
        place.setRowNumber(1);
        place.setPlaceNumberInRow(1);
        place.setHall(hall);

        Session session = new Session();
        session.setIdSession(1L);
        session.setMovie(movie);
        session.setPrice(150);
        session.setStartSession("10/00");

        Order test = new Order();
        test.setIdOrder(1L);
        test.setPlace(place);
        test.setSession(session);
        test.setBooking(Booking.RESERVATION);
        test.setUser(user);

        when(orderRepository.findByIdOrder(anyLong())).thenReturn(Optional.of(test));

        OrderDTOResponse res = orderService.get(test.getIdOrder());

        assertNotNull(res);
        assertEquals(res.getNumberHall(),test.getPlace().getHall().getNumberHall());
        assertEquals(res.getPrice(),test.getSession().getPrice());
        assertEquals(res.getNameMovie(),test.getSession().getMovie().getNameMovie());
        assertEquals(res.getUserDTO().getName(),test.getUser().getName());

    }

    @Test
    public void delete() {

        Order test = new Order();
        test.setIdOrder(1L);
        test.setPlace(new Place());
        test.setSession(new Session());
        test.setBooking(Booking.RESERVATION);
        test.setUser(new User());

        when(orderRepository.findByIdOrder(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        orderService.delete(1L);
        verify(orderRepository, Mockito.times(1)).save(test);// произошло ли одно взаимодействие с БД

    }

    @Test
    public void getOrder() {
    }

    @Test
    public void getAllOrder() {
        Integer page = 1;
        Integer perPage = 2;
        String sort = "idOrder";
        Sort.Direction order = Sort.Direction.DESC;

        List<Order> orders = getOrders();

        Page<Order> pageResult = mock(Page.class);

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(orders);

        List<OrderDTOResponse> res = orderService.getAllOrder(page, perPage, sort, order);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(orders.get(0).getBooking(), res.get(0).getBooking());
        assertEquals(orders.get(0).getPlace().getHall().getNumberHall(), res.get(0).getNumberHall());
    }



    @Test
    public void getAllOrderBySession() {
        List<Order> orders = getOrders();

        Long idSession = 1L;

        List<Order> ordersAfterFilter = orders.stream()
                .filter(o -> Objects.equals(o.getSession().getIdSession(), idSession))
                .collect(Collectors.toList());

        when(sessionService.getSession(anyLong())).thenReturn(orders.get(0).getSession());
        when(orderRepository.findOrderBySession(any(Session.class))).thenReturn(ordersAfterFilter);

        List<OrderDTOResponse> res = orderService.getAllOrderBySession(idSession);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(orders.get(0).getBooking(), res.get(0).getBooking());
        assertEquals(orders.get(0).getPlace().getHall().getNumberHall(), res.get(0).getNumberHall());
    }

    private List<Order> getOrders(){
        User user1 = new User();
        user1.setName("Ilya");
        user1.setEmail("Ilya@mail.ru");

        Cinema cinema1 = new Cinema();
        cinema1.setIdCinema(1L);
        cinema1.setHalls(10);
        cinema1.setNameCinema("Big");

        Movie movie1 = new Movie();
        movie1.setIdMovie(1L);
        movie1.setNameMovie("IT");

        Hall hall1 = new Hall();
        hall1.setIdHall(1L);
        hall1.setNumberHall(1);
        hall1.setPlaces(10);
        hall1.setRows(10);
        hall1.setCinema(cinema1);

        Place place1 = new Place();
        place1.setIdPlace(1L);
        place1.setRowNumber(1);
        place1.setPlaceNumberInRow(1);
        place1.setHall(hall1);

        Session session1 = new Session();
        session1.setIdSession(1L);
        session1.setMovie(movie1);
        session1.setPrice(150);
        session1.setStartSession("10/00");

        User user2 = new User();
        user2.setName("Ilya");
        user2.setEmail("Ilya@mail.ru");

        Cinema cinema2 = new Cinema();
        cinema2.setIdCinema(2L);
        cinema2.setHalls(10);
        cinema2.setNameCinema("Big");

        Movie movie2 = new Movie();
        movie2.setIdMovie(2L);
        movie2.setNameMovie("IT");

        Hall hall2 = new Hall();
        hall2.setIdHall(2L);
        hall2.setNumberHall(2);
        hall2.setPlaces(10);
        hall2.setRows(10);
        hall2.setCinema(cinema2);

        Place place2 = new Place();
        place2.setIdPlace(2L);
        place2.setRowNumber(1);
        place2.setPlaceNumberInRow(1);
        place2.setHall(hall2);

        Session session2 = new Session();
        session2.setIdSession(2L);
        session2.setMovie(movie2);
        session2.setPrice(150);
        session2.setStartSession("10/00");

        Order first = new Order();
        Order second = new Order();

        first.setIdOrder(1L);
        first.setPlace(place1);
        first.setSession(session1);
        first.setBooking(Booking.RESERVATION);
        first.setUser(user1);

        second.setIdOrder(2L);
        second.setPlace(place2);
        second.setSession(session2);
        second.setBooking(Booking.FREE);
        second.setUser(user2);

        return List.of(first, second);
    }
}