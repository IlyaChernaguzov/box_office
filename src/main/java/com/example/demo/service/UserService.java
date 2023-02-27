package com.example.demo.service;

import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.Place;
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
}
