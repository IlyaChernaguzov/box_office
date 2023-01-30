package com.example.demo.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    public static Pageable getPageRequest(Integer page, Integer perPage, String sort, Sort.Direction order){ // передается в бд
        if (page == null){ // номер страницы начинается с 0
            page = 0;
        } else if (page > 0) { // фронт передает номер страницы = 1
            page = page - 1;
        }

        if (perPage == null){ // если не передано количество элементов, то возвращаем 10 элементов
            perPage = 10;
        }

        if (order == null || sort == null){ // если не передан параметр сортировки и в каком из вариантов сортировать
            return PageRequest.of(page, perPage); // возвращем номер страницы и количество элементов
        } else if (order.equals("DESC")) {
            return PageRequest.of(page, perPage, Sort.by(Sort.Direction.DESC, sort));// сортирует по параметру sort в обратном порядке "DESC"
        } else {
            return PageRequest.of(page, perPage, Sort.by(Sort.Direction.ASC, sort));// сортирует по параметру sort в прямом порядке "ASC"
        }
    }
}
