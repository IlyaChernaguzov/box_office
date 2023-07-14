package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.CinemaDTO;
import com.example.demo.model.dto.CinemaDTOCreate;
import com.example.demo.model.entity.Cinema;
import com.example.demo.model.repository.CinemaRepository;
import com.example.demo.utils.PaginationUtils;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.ExpectedCount.times;

@RunWith(MockitoJUnitRunner.class)
public class CinemaServiceimplTest {

    @InjectMocks
    private CinemaServiceimpl cinemaService;

    @Mock
    private CinemaRepository cinemaRepository;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void create() {
        Cinema cinema = new Cinema();
        cinema.setIdCinema(1L);
        cinema.setNameCinema("Polis");
        cinema.setIndex("200911");
        cinema.setCity("Polock");
        cinema.setAddress("Gagarino 7");
        cinema.setHalls(5);


        CinemaDTOCreate test = new CinemaDTOCreate();
        test.setNameCinema("Polis");
        test.setIndex("200911");
        test.setCity("Polock");
        test.setAddress("Gagarino 7");
        test.setHalls(5);

        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);
        when(cinemaRepository.findByIdCinema(anyLong())).thenReturn(Optional.of(cinema));

//        when(cinemaRepository.save(any(Cinema.class)))
//                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        CinemaDTO res = cinemaService.create(test);
        assertEquals(test.getAddress(), res.getAddress());
        assertNotNull(res);

        //Mockito.verify(cinemaRepository, Mockito.times(1)).save(any(Cinema.class));

    }
    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionFindCinema() {
        CinemaDTOCreate test = new CinemaDTOCreate();
        test.setNameCinema("Polis");
        test.setIndex("200911");
        test.setCity("Polock");
        test.setAddress("Gagarino 7");
        test.setHalls(5);

        when(cinemaRepository.findByNameCinema(anyString())).thenThrow(CustomException.class);
        cinemaService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionNameCinemaIsNull() {
        CinemaDTOCreate test = new CinemaDTOCreate();
//        test.setIdCinema(1L);
        test.setNameCinema("");
        test.setIndex("200911");
        test.setCity("Polock");
        test.setAddress("Gagarino 7");
        test.setHalls(5);

//        when(cinemaRepository.findById(anyLong())).thenThrow(CustomException.class);
        cinemaService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionHallsIsNull() {
        CinemaDTOCreate test = new CinemaDTOCreate();
//        test.setIdCinema(1L);
        test.setNameCinema("Rodina");
        test.setIndex("200911");
        test.setCity("Polock");
        test.setAddress("Gagarino 7");
        test.setHalls(null);

//        when(cinemaRepository.findById(anyLong())).thenThrow(CustomException.class);
        cinemaService.create(test);
    }

    @Test
    public void update() {
        Cinema test = new Cinema();
        test.setIdCinema(1L);
        test.setNameCinema("Polis");
        test.setIndex("200911");
        test.setCity("Polock");
        test.setAddress("Gagarino 7");
        test.setHalls(5);

        when(cinemaRepository.findByIdCinema(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(cinemaRepository.save(any(Cinema.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        CinemaDTO testForUpdate = new CinemaDTO();
        testForUpdate.setIdCinema(1L);
        testForUpdate.setHalls(6);
        testForUpdate.setNameCinema(test.getNameCinema());

        CinemaDTO res = cinemaService.update(testForUpdate);

        assertNotNull(res);// проверяем, что он не ноль
        assertSame(res.getNameCinema(),test.getNameCinema());//сверяем значение результата и заданные аргументы метода
        assertEquals(res.getHalls(), testForUpdate.getHalls());//проверяем, обновилось ли Halls
        assertEquals(res.getCity(),test.getCity());//так как в экземпляре для обновления мы не задавали это поле, оно должно остаться прежним, проверяем это.

    }

    @Test
    public void get() {
        Cinema test = new Cinema();
        test.setIdCinema(1L);
        test.setNameCinema("Polis");
        test.setIndex("200911");
        test.setCity("Polock");
        test.setAddress("Gagarino 7");
        test.setHalls(5);

        when(cinemaRepository.findByIdCinema(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional

        CinemaDTO res = cinemaService.get(test.getIdCinema());

        assertNotNull(res);
        assertEquals(res.getCity(),test.getCity());

    }

    @Test
    public void delete() {
        Cinema test = new Cinema();
        test.setIdCinema(1L);
        test.setNameCinema("Polis");
        test.setIndex("200911");
        test.setCity("Polock");
        test.setAddress("Gagarino 7");
        test.setHalls(5);

        when(cinemaRepository.findByIdCinema(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(cinemaRepository.save(any(Cinema.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        cinemaService.delete(test.getIdCinema());
        verify(cinemaRepository, Mockito.times(1)).save(test);// произошло ли одно взаимодействие с БД

    }

    @Test
    public void getAllCinema() {
        Integer page = 1;
        Integer perPage = 2;
        String sort = "halls";
        Sort.Direction order = Sort.Direction.DESC;

        List<Cinema> cinemas = getCinemas();

        Page<Cinema> pageResult = mock(Page.class);

        when(cinemaRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(cinemas);

        List<CinemaDTO> res = cinemaService.getAllCinema(page, perPage, sort, order);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(cinemas.get(0).getHalls(), res.get(0).getHalls());

    }

    private List<Cinema> getCinemas(){
        Cinema first = new Cinema();
        Cinema second = new Cinema();

        first.setNameCinema("Polis");
        first.setIndex("200911");
        first.setCity("Polock");
        first.setAddress("Gagarino 7");
        first.setHalls(5);

        second.setNameCinema("Big");
        second.setIndex("190912");
        second.setCity("Minsk");
        second.setAddress("Beloruskay 10");
        second.setHalls(10);

        return List.of(first, second);
    }
}