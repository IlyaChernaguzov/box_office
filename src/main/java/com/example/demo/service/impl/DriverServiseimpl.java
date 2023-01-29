package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.entity.Driver;
import com.example.demo.model.enums.DriverStatus;
import com.example.demo.model.repository.DriverRepository;
import com.example.demo.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiseimpl implements DriverService {

    private final DriverRepository driverRepository; // прослойка с базой данных
    private final ObjectMapper mapper;


    @Override
    public DriverDTO create(DriverDTO driverDTO) { driverRepository.findByEmail(driverDTO.getEmail()).ifPresent(
            c -> {throw new CustomException("Водитель с email: " + driverDTO.getEmail() + "уже существует", HttpStatus.BAD_REQUEST); // создаем исключение "если Водитель уже существует, при создании выскачит ошибка"
            }
    );

        Driver driver = mapper.convertValue(driverDTO, Driver.class);
        driver.setCreatedAt(LocalDateTime.now());
        Driver save = driverRepository.save(driver);

        return mapper.convertValue(save, DriverDTO.class);

//        Driver driver = new Driver();
//        driver.setName(driverDTO.getName());
//        driver.setSurname(driverDTO.getSurname());
//        driver.setGender(driverDTO.getGender());

//        Driver driver = mapper.convertValue(driverDTO, Driver.class); //создали сущность из DTO
//
//        driver.setCreatedAt(LocalDateTime.now()); // устанавливаем текущее время
//
//        Driver save = driverRepository.save(driver);// сохранили сущность
//
//        DriverDTO result = mapper.convertValue(save, DriverDTO.class);// преоброзовали в DTO
//        return result;
    }

    @Override
    public DriverDTO update(DriverDTO driverDTO) {
        Driver driver = getDriver(driverDTO.getEmail()); // создаем исключение "если Водитель не найдено, при обновлении выскачит ошибка"

        driver.setName(driverDTO.getName() == null ? driver.getName() : driverDTO.getName()); // проверяем пришедшие данный на null с помощью "?". ":" - если не null, то присвиваем пришедшее значение
        driver.setSurname(driverDTO.getSurname() == null ? driver.getSurname() : driverDTO.getSurname());
        driver.setGender(driverDTO.getGender() == null ? driver.getGender() : driverDTO.getGender());
        driver.setEmail(driverDTO.getEmail() == null ? driver.getEmail() : driverDTO.getEmail());
        driver.setUpdatedAt(LocalDateTime.now());
        driver.setStatus(DriverStatus.UPDATED);
        Driver save = driverRepository.save(driver);

        return mapper.convertValue(save, DriverDTO.class);
    }

    @Override
    public DriverDTO get(String email) {
        Driver driver = getDriver(email);

        return mapper.convertValue(driver, DriverDTO.class);
    }

    @Override
    public void delete(String email) {
        Driver driver = getDriver(email);

        driver.setStatus(DriverStatus.DELETED);// установить статут DELETED
        driver.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        driverRepository.save(driver);
    }

    @Override
    public Driver getDriver(String email){
        return driverRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("Водитель с email: " + email + "не найден", HttpStatus.NOT_FOUND));

    }
}
