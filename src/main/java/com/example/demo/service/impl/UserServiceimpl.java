package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.Booking;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.PlaceStatus;
import com.example.demo.model.enums.UserStatus;
import com.example.demo.model.repository.OrderRepository;
import com.example.demo.model.repository.PlaceRepository;
import com.example.demo.model.repository.SessionRepository;
import com.example.demo.model.repository.UserRepository;
import com.example.demo.service.*;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.mail.internet.InternetAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceimpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;
    private final CinemaService cinemaService;
    private final HallService hallService;
    private final PlaceService placeService;
    private final OrderService orderService;
    private final PlaceRepository placeRepository;
    private final ObjectMapper mapper;

    @Override
    public UserDTO create(UserDTO userDTO) {

        try {
            InternetAddress emailAddr = new InternetAddress(userDTO.getEmail());
            emailAddr.validate();
        } catch (Exception ex) {
            log.error("[Create User] email is not valid" + userDTO);
            throw new CustomException("Невалидный email", HttpStatus.BAD_REQUEST);
        }

        userRepository.findByEmail(userDTO.getEmail()).ifPresent(
                c -> {throw new CustomException("Пользователь с эл.почтой: " + userDTO.getEmail() + " уже существует", HttpStatus.BAD_REQUEST);
                }
        );

        User user = mapper.convertValue(userDTO, User.class);
        user.setCreatedAt(LocalDateTime.now());
        User save = userRepository.save(user);
        return mapper.convertValue(save, UserDTO.class);
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        User user = getUser(userDTO.getEmail());

        user.setEmail(userDTO.getEmail() == null ? user.getEmail() : userDTO.getEmail());
        user.setName(userDTO.getName() == null ? user.getName() : userDTO.getName());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUserStatus(UserStatus.UPDATED);
        User save = userRepository.save(user);
        return mapper.convertValue(save, UserDTO.class);
    }

    @Override
    public UserDTO get(String email) {

        User user = getUser(email);
        return mapper.convertValue(user, UserDTO.class);
    }

    @Override
    public void delete(String email) {

        User user = getUser(email);
        user.setUserStatus(UserStatus.DELETED);
        user.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        userRepository.save(user);

    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("Пользователь с эл.почтой: " + email + " не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<UserDTO> getAllUser(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<User> pageResult = userRepository.findAll(pageRequest);

        List<UserDTO> collect = pageResult.getContent().stream()
                .map(c -> mapper.convertValue(c, UserDTO.class))
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<UserDTOResponsePlace> getAllOrderBySession(Long idSession) {

        List<Order> orders = orderRepository.findBySession(sessionService.getSession(idSession));

        List<PlaceDTORequest> places = orders.stream()
                .map(r -> mapper.convertValue(r.getPlace(), PlaceDTORequest.class))
                .collect(Collectors.toList());

        List<UserDTOResponsePlace> response = orders.stream()
                .map(r -> mapper.convertValue(r, UserDTOResponsePlace.class))
                .collect(Collectors.toList());

        for (int i = 0; i < response.size(); i++){
            response.get(i).setPlaceDTORequest(places.get(i));
        }
        return response;
    }

    @Override
    public List<UserDTOResponseSession> getAllSessionByCinema(String nameCinema) {
        List<Session> sessions = sessionRepository.findByCinema(cinemaService.getCinema(nameCinema));

        List<MovieDTOResponse> movies = sessions.stream()
                .map(r -> mapper.convertValue(r.getMovie(), MovieDTOResponse.class))
                .collect(Collectors.toList());

        List<Integer> halls = sessions.stream()
                .map(r -> r.getHall().getNumberHall())
                .collect(Collectors.toList());

        List<UserDTOResponseSession> response = sessions.stream()
                .map(r -> mapper.convertValue(r, UserDTOResponseSession.class))
                .collect(Collectors.toList());

        for (int i = 0; i < response.size(); i++){
            response.get(i).setMovieDTOResponse(movies.get(i));
        }

        for (int i = 0; i < response.size(); i++){
            response.get(i).setNumberHall(halls.get(i));
        }
        return response;
    }

    @Override
    public UserDTOResponseTicket getTicket(Long idSession, Long idPlace, String email) {
        Optional<Order> optional = orderRepository
                .findOrderBySessionAndPlace(sessionService.getSession(idSession), placeService.getPlace(idPlace));

        if (!optional.isPresent())
        {
            throw new CustomException("Место не найдено", HttpStatus.BAD_REQUEST);
        }

//        List<Order> orders = orderRepository.findBySession(sessionService.getSession(idSession))
//                .stream()
//                .filter(o -> Objects.equals(o.getPlace().getPlaceNumber(), placeNumber))
//                .collect(Collectors.toList());
//
//        if (orders.isEmpty())
//        {
//            throw new CustomException("Место не найдено", HttpStatus.BAD_REQUEST);
//        }

//        Order order = orders.get(0);
        Order order = optional.get();

        if (order.getBooking().equals(Booking.RESERVATION))
        {
            throw new CustomException("Это место уже забронировано", HttpStatus.BAD_REQUEST);
        }



        Long idOrder = order.getIdOrder();

        LocalDateTime startSession = order.getSession().getStartSession();
        Integer price = order.getSession().getPrice();
        String nameMovie = order.getSession().getMovie().getNameMovie();
        Integer durationMovie = order.getSession().getMovie().getDurationMovie();
        String nameCinema = order.getSession().getCinema().getNameCinema();
        Integer numberHall = order.getSession().getHall().getNumberHall();
        Integer rowNumber = order.getPlace().getRowNumber();
        Integer placeNumberInRow = order.getPlace().getPlaceNumberInRow();

        order.setBooking(Booking.RESERVATION);
        order.setUser(getUser(email));
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.UPDATED);
        Order save = orderRepository.save(order);

        UserDTOResponseTicket response = mapper.convertValue(save, UserDTOResponseTicket.class);
        response.setIdOrder(idOrder);
        response.setStartSession(startSession);
        response.setPrice(price);
        response.setNameMovie(nameMovie);
        response.setDurationMovie(durationMovie);
        response.setNameCinema(nameCinema);
        response.setNumberHall(numberHall);
        response.setRowNumber(rowNumber);
        response.setPlaceNumberInRow(placeNumberInRow);

        return response;
    }

    @Override
    public UserDTOResponseCancelBooking cancelBoking(Long idOrder) {
        Order order = orderService.getOrder(idOrder);

        order.setBooking(Booking.FREE);
        order.setUser(null);
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.UPDATED);
        Order save = orderRepository.save(order);

        UserDTOResponseCancelBooking result = mapper.convertValue(save, UserDTOResponseCancelBooking.class);
        result.setIdOrder(save.getIdOrder());
        result.setBooking(save.getBooking());

        return result;
    }

    @Override
    public List<UserDTOResponseOrder> getAllOrderByUser(String email) {
        List<Order> orders = orderRepository.findOrderByUser(getUser(email));

        if (orders.isEmpty())
        {
            throw new CustomException("У вас нет заказов", HttpStatus.BAD_REQUEST);
        }

//        List<Long> idOrders = orders.stream()
//                .map(i -> i.getIdOrder())
//                .collect(Collectors.toList());
//
//        List<LocalDateTime> startSessions = orders.stream()
//                .map(s -> s.getSession().getStartSession())
//                .collect(Collectors.toList());
//
//        List<Integer> prices = orders.stream()
//                .map(p -> p.getSession().getPrice())
//                .collect(Collectors.toList());
//
//        List<String> nameMovies = orders.stream()
//                .map(m -> m.getSession().getMovie().getNameMovie())
//                .collect(Collectors.toList());
//
//        List<Integer> durationMovies = orders.stream()
//                .map(d -> d.getSession().getMovie().getDurationMovie())
//                .collect(Collectors.toList());
//
//        List<String> nameCinemas = orders.stream()
//                .map(c -> c.getSession().getCinema().getNameCinema())
//                .collect(Collectors.toList());
//
//        List<Integer> numberHalls = orders.stream()
//                .map(h -> h.getSession().getHall().getNumberHall())
//                .collect(Collectors.toList());
//
//        List<Integer> rowNumbers = orders.stream()
//                .map(r -> r.getPlace().getRowNumber())
//                .collect(Collectors.toList());
//
//        List<Integer> placeNumbersInRow = orders.stream()
//                .map(p -> p.getPlace().getPlaceNumberInRow())
//                .collect(Collectors.toList());
//
//        List<UserDTOResponseOrder> response = orders.stream()
//                .map(r -> mapper.convertValue(r, UserDTOResponseOrder.class))
//                .collect(Collectors.toList());
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setIdOrder(idOrders.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setStartSession(startSessions.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setPrice(prices.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setNameMovie(nameMovies.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setDurationMovie(durationMovies.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setNameCinema(nameCinemas.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setNumberHall(numberHalls.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setRowNumber(rowNumbers.get(i));
//        }
//
//        for (int i = 0; i < response.size(); i++){
//            response.get(i).setPlaceNumberInRow(placeNumbersInRow.get(i));
//        }

        return orders
                .stream()
                .map(h ->
                {
                    UserDTOResponseOrder response = new UserDTOResponseOrder();
                    response.setIdOrder (h
                            .getIdOrder()
                    );
                    response.setStartSession(h.getSession().getStartSession());
                    response.setPrice(h.getSession().getPrice());
                    response.setNameMovie(h.getSession().getMovie().getNameMovie());
                    response.setDurationMovie(h.getSession().getMovie().getDurationMovie());
                    response.setNameCinema(h.getSession().getCinema().getNameCinema());
                    response.setNumberHall(h.getSession().getHall().getNumberHall());
                    response.setRowNumber(h.getPlace().getRowNumber());
                    response.setPlaceNumberInRow(h.getPlace().getPlaceNumberInRow());

                    return response;
                })
                .collect(Collectors.toList());

//        return response;
    }
}
