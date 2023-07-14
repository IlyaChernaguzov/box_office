package com.example.demo.service.impl;

import com.example.demo.exceptions.CustomException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.repository.PlaceRepository;
import com.example.demo.service.HallService;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlaceServiceimplTest {

    @InjectMocks
    private PlaceServiceimpl placeService;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private HallService hallService;
    @Spy
    private ObjectMapper mapper;

    @Test
    public void create() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        Place place = new Place();
        place.setIdPlace(1L);
        place.setRowNumber(1);
        place.setPlaceNumberInRow(1);
        place.setHall(hall);

        when(hallService.getHall(anyLong())).thenReturn(hall);
        when(placeRepository.save(any(Place.class))).thenReturn(place);
        when(placeRepository.findByIdPlace(anyLong())).thenReturn(Optional.of(place));

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(1);
        test.setPlaceNumberInRow(1);
        test.setIdHall(hall.getIdHall());

        PlaceDTO res = placeService.create(test);
        assertEquals(test.getIdHall(), res.getIdHall());
        assertEquals(test.getRowNumber(), res.getRowNumber());
        assertNotNull(res);

    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionFindPlace() {

        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(1);
        test.setPlaceNumberInRow(1);
        test.setIdHall(hall.getIdHall());


        when(placeRepository
                .findPlaceByHallAndRowNumberAndPlaceNumberInRow(any(Hall.class), anyInt(), anyInt()))
                .thenThrow(CustomException.class);
        placeService.create(test);

    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionQuantityRowsInHall() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(11);
        test.setPlaceNumberInRow(1);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionQuantityRows() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(0);
        test.setPlaceNumberInRow(1);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionRowsIsNull() {
        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(null);

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionQuantityPlaceNumberInRowInHall() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(10);
        test.setPlaceNumberInRow(11);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionQuantityPlaceNumberInRow() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(1);
        test.setPlaceNumberInRow(0);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void create_exceptionPlaceNumberInRowIsNull() {
        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setPlaceNumberInRow(null);

        placeService.create(test);
    }

    @Test
    public void update() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setRows(10);
        hall.setPlaces(10);

        Place test = new Place();
        test.setIdPlace(1L);
        test.setRowNumber(1);
        test.setPlaceNumberInRow(1);
        test.setHall(hall);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        when(placeRepository.findByIdPlace(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
//        when(hallRepository.save(test)).thenReturn(test);
        when(placeRepository.save(any(Place.class)))
                .thenAnswer(i -> i.getArguments()[0]);// имитация сохранения в бд

        PlaceDTO testForUpdate = new PlaceDTO();
        testForUpdate.setIdPlace(1L);
        testForUpdate.setRowNumber(2);
//        testForUpdate.setPlaceNumberInRow(test.getPlaceNumberInRow());
        testForUpdate.setIdHall(test.getHall().getIdHall());

        PlaceDTO res = placeService.update(testForUpdate);

        assertNotNull(res);// проверяем, что он не ноль
        //assertSame(res.getRows(),test.getRows());//сверяем значение результата и заданные аргументы метода
        assertEquals(res.getRowNumber(), testForUpdate.getRowNumber());//проверяем, обновилось ли Halls
        assertEquals(res.getPlaceNumberInRow(),test.getPlaceNumberInRow());//так как в экземпляре для обновления мы не задавали это поле, оно должно остаться прежним, проверяем это.
        //assertEquals(res.getIdCinema(),test.getCinema().getIdCinema());
        assertEquals(res.getIdHall(),testForUpdate.getIdHall());
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void update_exceptionQuantityRowsInHall() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(11);
        test.setPlaceNumberInRow(1);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void cupdate_exceptionQuantityRows() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(0);
        test.setPlaceNumberInRow(1);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void update_exceptionQuantityPlaceNumberInRowInHall() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(10);
        test.setPlaceNumberInRow(11);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test(expected = CustomException.class)// проверка на исключение (ожидаемполучить исключение)
    public void update_exceptionQuantityPlaceNumberInRow() {
        Hall hall = new Hall();
        hall.setIdHall(1L);
        hall.setNumberHall(1);
        hall.setPlaces(10);
        hall.setRows(10);

        when(hallService.getHall(anyLong())).thenReturn(hall);

        PlaceDTOCreate test = new PlaceDTOCreate();
        test.setRowNumber(1);
        test.setPlaceNumberInRow(0);
        test.setIdHall(hall.getIdHall());

        placeService.create(test);
    }

    @Test
    public void get() {
    }

    @Test
    public void delete() {
        Place test = new Place();
        test.setIdPlace(1L);

        when(placeRepository.findByIdPlace(anyLong())).thenReturn(Optional.of(test)); //иммитирует бд, что при поиске по имени вернет объект типа Optional
        when(placeRepository.save(any(Place.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        placeService.delete(test.getIdPlace());
        verify(placeRepository, Mockito.times(1)).save(test);// произошло ли одно взаимодействие с БД
    }

    @Test
    public void getPlace() {
    }

    @Test
    public void getAllPlace() {
        Integer page = 1;
        Integer perPage = 2;
        String sort = "idPlace";
        Sort.Direction order = Sort.Direction.DESC;

        List<Place> places = getPlaces();

        Page<Place> pageResult = mock(Page.class);

        when(placeRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(pageResult.getContent()).thenReturn(places);

        List<PlaceDTO> res = placeService.getAllPlace(page, perPage, sort, order);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(places.get(0).getIdPlace(), res.get(0).getIdPlace());
        assertEquals(places.get(0).getHall().getIdHall(), res.get(0).getIdHall());
    }

    @Test
    public void getAllPlaceByHall() {
        List<Place> places = getPlaces();

        Long idHall = 1L;

        Hall hall = new Hall();
        hall.setIdHall(idHall);

        Place test = new Place();
        test.setIdPlace(1L);
        test.setRowNumber(1);
        test.setPlaceNumberInRow(1);
        test.setHall(hall);

        List<Place> placesAfterFilter = places.stream()
                .filter(o -> Objects.equals(o.getHall().getIdHall(), test.getHall().getIdHall()))
                .collect(Collectors.toList());

        when(hallService.getHall(anyLong())).thenReturn(places.get(0).getHall());
        when(placeRepository.findByHall(any(Hall.class))).thenReturn(placesAfterFilter);

        List<PlaceDTO> res = placeService.getAllPlaceByHall(places.get(0).getHall().getIdHall());

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(places.get(0).getIdPlace(), res.get(0).getIdPlace());
        assertEquals(places.get(0).getHall().getIdHall(), res.get(0).getIdHall());
    }

    private List<Place> getPlaces(){
        Place first = new Place();
        Place second = new Place();

        Hall hall1 = new Hall();
        hall1.setIdHall(1L);
        hall1.setNumberHall(1);
        hall1.setPlaces(10);
        hall1.setRows(10);

        Hall hall2 = new Hall();
        hall2.setIdHall(2L);
        hall2.setNumberHall(2);
        hall2.setPlaces(20);
        hall2.setRows(20);

        first.setIdPlace(1L);
        first.setRowNumber(1);
        first.setPlaceNumberInRow(1);
        first.setHall(hall1);

        second.setIdPlace(2L);
        second.setRowNumber(1);
        second.setPlaceNumberInRow(1);
        second.setHall(hall2);

        return List.of(first, second);
    }
}