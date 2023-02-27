package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.DriverDTO;
import com.example.demo.model.entity.Place;
import com.example.demo.model.enums.PlaceStatus;
import com.example.demo.model.repository.DriverRepository;
import com.example.demo.service.DriverService;
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
public class DriverServiseimpl implements DriverService {

    private final DriverRepository driverRepository; // прослойка с базой данных
    private final ObjectMapper mapper;


    @Override
    public DriverDTO create(DriverDTO driverDTO) { driverRepository.findByEmail(driverDTO.getEmail()).ifPresent(
            c -> {throw new CustomException("Водитель с email: " + driverDTO.getEmail() + " уже существует", HttpStatus.BAD_REQUEST); // создаем исключение "если Водитель уже существует, при создании выскачит ошибка"
            }
    );

        Place driver = mapper.convertValue(driverDTO, Place.class);
        driver.setCreatedAt(LocalDateTime.now());
        Place save = driverRepository.save(driver);

        return mapper.convertValue(save, DriverDTO.class);

        // если значения не совпадают в запросе DTO и классе, то присваиваем значения вручную. Для дат требуется обработка исключений
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
        Place driver = getDriver(driverDTO.getEmail()); // создаем исключение "если Водитель не найдено, при обновлении выскачит ошибка"

        driver.setName(driverDTO.getName() == null ? driver.getName() : driverDTO.getName()); // проверяем пришедшие данный на null с помощью "?". ":" - если не null, то присвиваем пришедшее значение
        driver.setSurname(driverDTO.getSurname() == null ? driver.getSurname() : driverDTO.getSurname());
        driver.setGender(driverDTO.getGender() == null ? driver.getGender() : driverDTO.getGender());
        driver.setEmail(driverDTO.getEmail() == null ? driver.getEmail() : driverDTO.getEmail());
        driver.setUpdatedAt(LocalDateTime.now());
        driver.setStatus(PlaceStatus.UPDATED);
        Place save = driverRepository.save(driver);

        return mapper.convertValue(save, DriverDTO.class);
    }

    @Override
    public DriverDTO get(String email) {
        Place driver = getDriver(email);

        return mapper.convertValue(driver, DriverDTO.class);
    }

    @Override
    public void delete(String email) {
        Place driver = getDriver(email);

        driver.setStatus(PlaceStatus.DELETED);// установить статут DELETED
        driver.setUpdatedAt(LocalDateTime.now());
//        driverRepository.delete(driver);// полное удаление
        driverRepository.save(driver);
    }

    @Override
    public Place getDriver(String email){
        return driverRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("Водитель с email: " + email + " не найден", HttpStatus.NOT_FOUND));

    }

    @Override
    public List<DriverDTO> getAllDrivers(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);//используем метод getPageRequest класса PaginationUtils для проверки полученных параметров
        Page<Place> pageResult = driverRepository.findAll(pageRequest);// получает отсортированную страницу в формате Page из бд, согласно прешедшим параметрам. Из параметров Page можем вернуть только номер страницы и количество элементов

        List<DriverDTO> collect = pageResult.getContent().stream()// получаем все значения
                .map(c -> mapper.convertValue(c, DriverDTO.class))// конвентируем все значения в DriverDTO
                .collect(Collectors.toList()); // собираем в список

//        PagedListHolder<DriverDTO> result = new PagedListHolder<>();//PagedListHolder может передать больше данных

        return collect;
    }
}
