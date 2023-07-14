package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.HallDTO;
import com.example.demo.model.dto.HallDTOCreate;
import com.example.demo.model.entity.Cinema;
import com.example.demo.model.entity.Hall;
import com.example.demo.model.repository.HallRepository;
import com.example.demo.service.CinemaService;
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
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HallServiceimplTest {

    @InjectMocks
    private HallServiceimpl hallService;

    @Mock
    private HallRepository hallRepository;

    @Mock
    private CinemaService cinemaService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void create() {

        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);
        cinema.setHalls(10);

        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);

        when(hallRepository.save(any(Hall.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        HallDTOCreate test = new HallDTOCreate();
//        test.setIdHall();
        test.setNumberHall(1);
        test.setPlaces(10);
        test.setRows(10);
        test.setIdCinema(1L);

        HallDTO res = hallService.create(test);
        assertEquals(test.getNumberHall(), res.getNumberHall());
        assertEquals(test.getIdCinema(), res.getIdCinema());
        assertNotNull(res);

    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionFindHall() {
        HallDTOCreate test = new HallDTOCreate();
//        test.setIdHall();
        test.setNumberHall(1);
        test.setPlaces(10);
        test.setRows(10);
        test.setIdCinema(1L);

        Cinema cinema = new Cinema();
        cinema.setHalls(10);

        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);

        when(hallRepository.findHallByCinemaAndNumberHall(any(Cinema.class), anyInt())).thenThrow(CustomException.class);
        hallService.create(test);

    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionQuantityHallsInCinema() {
        HallDTOCreate test = new HallDTOCreate();
        test.setNumberHall(20);
        test.setPlaces(10);
        test.setRows(10);
        test.setIdCinema(1L);

        Cinema cinema = new Cinema();
        cinema.setHalls(10);

        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);
        hallService.create(test);


    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionQuantityHalls() {

        HallDTOCreate test = new HallDTOCreate();
        test.setNumberHall(0);
        test.setPlaces(10);
        test.setRows(10);
        test.setIdCinema(1L);

        Cinema cinema = new Cinema();
        cinema.setHalls(10);

        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);
        hallService.create(test);

    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionNumberHallIsNull() {

        HallDTOCreate test = new HallDTOCreate();
        test.setNumberHall(null);
        test.setPlaces(10);
        test.setRows(10);
        test.setIdCinema(1L);

//        Cinema cinema = new Cinema();
//        cinema.setHalls(10);

//        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);
        hallService.create(test);

    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionRowsIsNull() {

        HallDTOCreate test = new HallDTOCreate();
        test.setNumberHall(1);
        test.setPlaces(10);
        test.setRows(null);
        test.setIdCinema(1L);

//        Cinema cinema = new Cinema();
//        cinema.setHalls(10);

//        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);
        hallService.create(test);

    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionPlacesIsNull() {

        HallDTOCreate test = new HallDTOCreate();
        test.setNumberHall(1);
        test.setPlaces(null);
        test.setRows(10);
        test.setIdCinema(1L);

//        Cinema cinema = new Cinema();
//        cinema.setHalls(10);

//        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);
        hallService.create(test);

    }

    @Test
    public void update() {
        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);

        Hall test = new Hall();
        test.setIdHall(1L);
        test.setNumberHall(1);
        test.setPlaces(10);
        test.setRows(10);
        test.setCinema(cinema);

        when(cinemaService.getCinema(anyLong())).thenReturn(cinema);

        when(hallRepository.findByIdHall(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
//        when(hallRepository.save(test)).thenReturn(test);
        when(hallRepository.save(any(Hall.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        HallDTO testForUpdate = new HallDTO();
        testForUpdate.setIdHall(1L);
        testForUpdate.setPlaces(15);
        testForUpdate.setRows(test.getRows());
        testForUpdate.setIdCinema(test.getCinema().getIdCinema());

        HallDTO res = hallService.update(testForUpdate);

        assertNotNull(res);// проверяем, что он не ноль
        assertSame(res.getRows(),test.getRows());//сверяем значение результата и заданные аргументы метода
        assertEquals(res.getPlaces(), testForUpdate.getPlaces());//проверяем, обновилось ли Halls
        assertEquals(res.getNumberHall(),test.getNumberHall());//так как в экземпляре для обновления мы не задавали это поле, оно должно остаться прежним, проверяем это.
        //assertEquals(res.getIdCinema(),test.getCinema().getIdCinema());
        assertEquals(res.getIdCinema(),testForUpdate.getIdCinema());

    }

    @Test
    public void get() {

        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);
        cinema.setHalls(10);

        Hall test = new Hall();
        test.setIdHall(1L);
        test.setNumberHall(10);
        test.setPlaces(10);
        test.setRows(10);
        test.setCinema(cinema);

        when(hallRepository.findByIdHall(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional

        HallDTO res = hallService.get(test.getIdHall());

        assertNotNull(res);
        assertEquals(res.getNumberHall(),test.getNumberHall());
    }

    @Test
    public void delete() {
        Hall test = new Hall();
        test.setIdHall(1L);
        test.setNumberHall(10);
        test.setPlaces(10);
        test.setRows(10);

        when(hallRepository.findByIdHall(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(hallRepository.save(any(Hall.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        hallService.delete(1L);
        verify(hallRepository, Mockito.times(1)).save(test);// произошло ли одно взаимодействие с БД

    }

    @Test
    public void getHall() {
    }

    @Test
    public void getAllHall() {
        Integer page = 1;
        Integer perPage = 2;
        String sort = "idHall";
        Sort.Direction order = Sort.Direction.DESC;

        List<Hall> halls = getHalls();

        Page<Hall> pageResult = mock(Page.class);

        when(hallRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(halls);

        List<HallDTO> res = hallService.getAllHall(page, perPage, sort, order);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(halls.get(0).getNumberHall(), res.get(0).getNumberHall());
    }
    private List<Hall> getHalls(){
        Hall first = new Hall();
        Hall second = new Hall();

        Cinema cinema1 = new Cinema();
        cinema1.setIdCinema(1L);
        cinema1.setHalls(10);
        cinema1.setNameCinema("Big");

        Cinema cinema2 = new Cinema();
        cinema2.setIdCinema(2L);
        cinema2.setHalls(10);
        cinema2.setNameCinema("Big");

        first.setIdHall(1L);
        first.setNumberHall(11);
        first.setPlaces(10);
        first.setRows(10);
        first.setCinema(cinema1);

        second.setIdHall(2L);
        second.setNumberHall(21);
        second.setPlaces(20);
        second.setRows(20);
        second.setCinema(cinema2);

        return List.of(first, second);
    }

}