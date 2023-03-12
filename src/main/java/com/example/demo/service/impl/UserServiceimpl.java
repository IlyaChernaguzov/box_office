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
//    private final OrderService orderService;
    private final PlaceRepository placeRepository;
    private final ObjectMapper mapper;

    @Override
    public UserDTO create(UserDTO userDTO) {
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
        user.setAge(userDTO.getAge() == null ? user.getAge() : userDTO.getAge());
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
    public List<UserDTOResponsePlace> getAllOrderBySession(String sessionNumber) {


        List<Order> orders = orderRepository.findBySession(sessionService.getSession(sessionNumber));

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

        List<HallDTORequest> halls = sessions.stream()
                .map(r -> mapper.convertValue(r.getHall(), HallDTORequest.class))
                .collect(Collectors.toList());

        List<UserDTOResponseSession> response = sessions.stream()
                .map(r -> mapper.convertValue(r, UserDTOResponseSession.class))
                .collect(Collectors.toList());

        for (int i = 0; i < response.size(); i++){
            response.get(i).setMovieDTOResponse(movies.get(i));
        }

        for (int i = 0; i < response.size(); i++){
            response.get(i).setHallDTORequest(halls.get(i));
        }
        return response;
    }

    @Override
    public UserDTOResponseTicket getTicket(String sessionNumber, Integer placeNumber) {
        List<Order> orders = orderRepository.findBySession(sessionService.getSession(sessionNumber))
                .stream()
                .filter(o -> Objects.equals(o.getPlace().getPlaceNumber(), placeNumber))
                .collect(Collectors.toList());

        if (orders.isEmpty())
        {
            throw new CustomException("Место не найдено", HttpStatus.BAD_REQUEST);
        }

        Order order = orders.get(0);

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

//    @Override
//    public UserDTOResponsePlace getPlace(UserDTORequestPlace userDTORequestPlace) {
//
//        Session session = sessionService.getSession(userDTORequestPlace.getSessionNumber());
//
//        HallDTORequest hall = mapper.convertValue(session.getHall(), HallDTORequest.class);
//        UserDTOResponsePlace result = mapper.convertValue(session, UserDTOResponsePlace.class);
//        result.setHallDTORequest(hall);
//        List<PlaceDTORequest> placeDTORequest = placeService.getAllPlaceByHall(hall.getNumberHall());
//        result.setPlaceDTORequest(placeDTORequest);
//
//        return result;
//    }
//
//    @Override
//    public UserDTOResponseBooking bookingPlace(UserDTORequestBooking userDTORequestBooking) {
//
//        Place place = placeService.getPlace(userDTORequestBooking.getPlaceNumber());
////        place.setBooking(Booking.RESERVATION);
//        place.setUpdatedAt(LocalDateTime.now());
//        place.setPlaceStatus(PlaceStatus.UPDATED);
//        placeRepository.save(place);
//
//        String message = "Введите платежную инфрмацию";
//        UserDTOResponseBooking result = mapper.convertValue(message, UserDTOResponseBooking.class);
//        return result;
//    }
}
