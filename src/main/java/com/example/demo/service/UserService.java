package com.example.demo.service;

import com.example.demo.model.dto.*;
import com.example.demo.model.entity.User;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserService {

    UserDTO create(UserDTO userDTO);

    UserDTO update(UserDTO userDTO);

    UserDTO get(String email);

    void delete(String email);

    User getUser(String email);

    List<UserDTO> getAllUser(Integer page, Integer perPage, String sort, Sort.Direction order);

//    UserDTOResponsePlace getPlace(UserDTORequestPlace userDTORequestPlace);
//
//    UserDTOResponseBooking bookingPlace (UserDTORequestBooking userDTORequestBooking);

    List<UserDTOResponsePlace> getAllOrderBySession (Long idSession);

    List<UserDTOResponseSession> getAllSessionByCinema (String nameCinema);

    UserDTOResponseTicket getTicket (Long idSession, Long idPlace, String email);

    UserDTOResponseCancelBooking cancelBoking (Long idOrder);

    List<UserDTOResponseOrder> getAllOrderByUser (String email);

}
