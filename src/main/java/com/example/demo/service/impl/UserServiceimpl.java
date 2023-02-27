package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.PlaceDTO;
import com.example.demo.model.dto.SessionDTO;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.Place;
import com.example.demo.model.entity.Session;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.PlaceStatus;
import com.example.demo.model.enums.UserStatus;
import com.example.demo.model.repository.HallRepository;
import com.example.demo.model.repository.UserRepository;
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
public class UserServiceimpl implements UserService {

    private final UserRepository userRepository;
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
}
