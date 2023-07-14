package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.Booking;
import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.Rating;
import com.example.demo.model.repository.OrderRepository;
import com.example.demo.model.repository.SessionRepository;
import com.example.demo.model.repository.UserRepository;
import com.example.demo.service.CinemaService;
import com.example.demo.service.MovieService;
import com.example.demo.service.OrderService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceimplTest {

    @InjectMocks
    private UserServiceimpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionService sessionService;
    @Mock
    private CinemaService cinemaService;
    @Mock
    private MovieService movieService;
    @Mock
    private OrderService orderService;
    @Spy
    private ObjectMapper mapper;

    @Test
    public void create() {

        UserDTO test = new UserDTO();
        test.setEmail("fff@FF.ru");
        test.setName("Ilya");


       // when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        UserDTO res = userService.create(test);
        assertEquals(test.getName(), res.getName());
        assertNotNull(res);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionFindUser() {
        UserDTO test = new UserDTO();
        test.setEmail("fff@FF.ru");
        test.setName("Ilya");

        when(userRepository.findByEmail(anyString())).thenThrow(CustomException.class);
        userService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionValidateUser() {
        UserDTO test = new UserDTO();
        test.setEmail("fff@");
        test.setName("Ilya");

        userService.create(test);
    }

    @Test
    public void update() {
        User test = new User();
        test.setEmail("hhh@JJ.com");
        test.setEmail("Ilya");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        UserDTO testForUpdate = new UserDTO();
        testForUpdate.setEmail("hhh@JJ.com");
        testForUpdate.setName("Ilya");

        UserDTO res = userService.update(testForUpdate);
        assertNotNull(res);
        assertSame(res.getName(),testForUpdate.getName());
        assertEquals(res.getEmail(), test.getEmail());
    }

    @Test
    public void get() {
        User test = new User();
        test.setEmail("hhh@JJ.com");
        test.setEmail("Ilya");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(test));

        UserDTO res = userService.get("hhh@JJ.com");
        assertNotNull(res);
        assertEquals(res.getName(),test.getName());
    }

    @Test
    public void delete() {
        User test = new User();
        test.setEmail("hhh@JJ.com");
        test.setEmail("Ilya");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        userService.delete("hhh@JJ.com");
        verify(userRepository, Mockito.times(1)).save(test);// произошло ли одно взаимодействие с БД
    }

    @Test
    public void getUser() {
    }

    @Test
    public void getAllUser() {
        Integer page = 1;
        Integer perPage = 2;
        String sort = "email";
        Sort.Direction order = Sort.Direction.DESC;

        List<User> users = getUsers();

        Page<User> pageResult = mock(Page.class);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(users);

        List<UserDTO> res = userService.getAllUser(page, perPage, sort, order);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(users.get(0).getEmail(), res.get(0).getEmail());
    }

    @Test
    public void getAllOrderBySession() {
        Long idSession = 1L;
        List<Order> orders = getOrders();

        List<Order> ordersAfterFilter = orders.stream()
                .filter(o -> Objects.equals(o.getSession().getIdSession(), idSession))
                .collect(Collectors.toList());

        when(sessionService.getSession(anyLong())).thenReturn(orders.get(0).getSession());
        when(orderRepository.findOrderBySession(any(Session.class))).thenReturn(ordersAfterFilter);

        List<UserDTOResponsePlace> res = userService.getAllOrderBySession(idSession);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(orders.get(0).getBooking(), res.get(0).getBooking());
        assertEquals(orders.get(0).getPlace().getRowNumber(), res.get(0).getPlaceNumberInRow());
    }

    @Test
    public void getAllSessionByCinema() {

        Long idCinema = 1L;
        List<Session> sessions = getSessions();

        List<Session> sessionsAfterFilter = sessions.stream()
                .filter(o -> Objects.equals(o.getCinema().getIdCinema(), idCinema))
                .collect(Collectors.toList());

        when(cinemaService.getCinema(anyLong())).thenReturn(sessions.get(0).getCinema());
        when(sessionRepository.findByCinema(any(Cinema.class))).thenReturn(sessionsAfterFilter);

        List<UserDTOResponseSession> res = userService.getAllSessionByCinema(idCinema);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(sessions.get(0).getPrice(), res.get(0).getPrice());
        assertEquals(sessions.get(0).getCinema().getNameCinema(), res.get(0).getNameCinema());
        assertEquals(sessions.get(0).getHall().getNumberHall(), res.get(0).getNumberHall());
        assertEquals(sessions.get(0).getMovie().getNameMovie(), res.get(0).getNameMovie());
    }

    @Test
    public void getAllSessionByMovie() {
        Long idMovie = 1L;
        List<Session> sessions = getSessions();

        List<Session> sessionsAfterFilter = sessions.stream()
                .filter(o -> Objects.equals(o.getMovie().getIdMovie(), idMovie))
                .collect(Collectors.toList());

        when(movieService.getMovie(anyLong())).thenReturn(sessions.get(0).getMovie());
        when(sessionRepository.findByMovie(any(Movie.class))).thenReturn(sessionsAfterFilter);

        List<UserDTOResponseSession> res = userService.getAllSessionByMovie(idMovie);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(sessions.get(0).getPrice(), res.get(0).getPrice());
        assertEquals(sessions.get(0).getCinema().getNameCinema(), res.get(0).getNameCinema());
        assertEquals(sessions.get(0).getHall().getNumberHall(), res.get(0).getNumberHall());
        assertEquals(sessions.get(0).getMovie().getNameMovie(), res.get(0).getNameMovie());
    }

    @Test
    public void getTicket() {

        Order order = getOrders().get(0);

        Order test = new Order();
        test.setIdOrder(1L);
        test.setPlace(order.getPlace());
        test.setSession(order.getSession());
        test.setBooking(Booking.RESERVATION);
        test.setUser(order.getUser());

        when(orderService.getOrder(anyLong())).thenReturn(order);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(order.getUser()));
        when(orderRepository.save(any(Order.class))).thenReturn(test);

        UserDTOResponseTicket res = userService.getTicket(order.getIdOrder(), order.getUser().getEmail());
        assertNotNull(res);
        assertEquals(order.getSession().getPrice(), res.getPrice());
        assertEquals(order.getSession().getMovie().getNameMovie(), res.getNameMovie());
        assertEquals(order.getSession().getHall().getNumberHall(), res.getNumberHall());
        assertEquals(order.getSession().getHall().getCinema().getNameCinema(), res.getNameCinema());
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void getTicket_exceptionPlaceReservation() {

        User user = new User();
        user.setName("Ilya");
        user.setEmail("Ilya@mail.ru");

        Order order = new Order();
        order.setIdOrder(1L);
        order.setPlace(new Place());
        order.setSession(new Session());
        order.setBooking(Booking.RESERVATION);
        order.setUser(user);

        when(orderService.getOrder(anyLong())).thenReturn(order);
        userService.getTicket(order.getIdOrder(), order.getUser().getEmail());
    }



    @Test
    public void cancelBoking() {

        Order order = getOrders().get(1);

        Order test = new Order();
        test.setIdOrder(order.getIdOrder());
        test.setPlace(order.getPlace());
        test.setSession(order.getSession());
        test.setBooking(Booking.FREE);
        test.setUser(null);

        when(orderService.getOrder(anyLong())).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(test);

        UserDTOResponseCancelBooking res = userService.cancelBoking(order.getIdOrder());

        assertNotNull(res);
        assertEquals(order.getIdOrder(), res.getIdOrder());
        assertEquals(test.getBooking(), res.getBooking());

    }

    @Test
    public void getAllOrderByUser() {
        String email = "Ilya@mail.ru";
        List<Order> orders = getOrders();

        List<Order> ordersAfterFilter = orders.stream()
                .filter(o -> Objects.equals(o.getUser().getEmail(), email))
                .collect(Collectors.toList());

        when(orderRepository.findOrderByUser(any(User.class))).thenReturn(ordersAfterFilter);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(orders.get(0).getUser()));

        List<UserDTOResponseOrder> res = userService.getAllOrderByUser(email);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(orders.get(0).getSession().getPrice(), res.get(0).getPrice());
        assertEquals(orders.get(0).getSession().getMovie().getNameMovie(), res.get(0).getNameMovie());
        assertEquals(orders.get(0).getSession().getHall().getNumberHall(), res.get(0).getNumberHall());
        assertEquals(orders.get(0).getSession().getHall().getCinema().getNameCinema(), res.get(0).getNameCinema());
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void getAllOrderByUser_exceptionTiketNotFound() {

        User user = new User();
        user.setName("Ilya");
        user.setEmail("Ilya@mail.ru");

        List<Order> orders = new ArrayList<>();

        when(orderRepository.findOrderByUser(any(User.class))).thenReturn(orders);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        userService.getAllOrderByUser(user.getEmail());
    }



    private List<User> getUsers(){
        User first = new User();
        User second = new User();

        first.setName("Ilya");
        first.setEmail("ggg@mail.ru");

        second.setName("Tany");
        second.setEmail("ttt@mail.ru");

        return List.of(first, second);
    }

    private List<Session> getSessions(){
        Session first = new Session();
        Session second = new Session();

        Cinema cinema1 = new Cinema();
        cinema1.setIdCinema(1L);
        cinema1.setNameCinema("Big");

        Hall hall1 = new Hall();
        hall1.setIdHall(1L);
        hall1.setCinema(cinema1);
        hall1.setNumberHall(1);

        Movie movie1 = new Movie();
        movie1.setIdMovie(1L);
        movie1.setNameMovie("IT");
        movie1.setDurationMovie(120);
        movie1.setRatingMovie(Rating.RARS_18);
        movie1.setGenreMovie(Genre.THRILLER);

        Cinema cinema2 = new Cinema();
        cinema2.setIdCinema(2L);
        cinema2.setNameCinema("Small");

        Hall hall2 = new Hall();
        hall2.setIdHall(2L);
        hall2.setCinema(cinema2);
        hall2.setNumberHall(2);

        Movie movie2 = new Movie();
        movie2.setIdMovie(2L);
        movie2.setNameMovie("Seven");
        movie2.setDurationMovie(90);
        movie2.setRatingMovie(Rating.RARS_16);
        movie2.setGenreMovie(Genre.THRILLER);

        first.setIdSession(1L);
        first.setStartSession("10/00");
        first.setPrice(100);
        first.setHall(hall1);
        first.setMovie(movie1);
        first.setCinema(hall1.getCinema());

        second.setIdSession(2L);
        second.setStartSession("20/00");
        second.setPrice(200);
        second.setHall(hall2);
        second.setMovie(movie2);
        second.setCinema(hall2.getCinema());

        return List.of(first, second);
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
        session1.setHall(hall1);

        User user2 = new User();
        user2.setName("Tany");
        user2.setEmail("Tany@mail.ru");

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
        session2.setHall(hall2);

        Order first = new Order();
        Order second = new Order();

        first.setIdOrder(1L);
        first.setPlace(place1);
        first.setSession(session1);
        first.setBooking(Booking.FREE);
        first.setUser(user1);

        second.setIdOrder(2L);
        second.setPlace(place2);
        second.setSession(session2);
        second.setBooking(Booking.RESERVATION);
        second.setUser(user2);

        return List.of(first, second);
    }
}