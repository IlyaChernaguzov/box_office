package com.example.demo.model.repository;

import com.example.demo.model.entity.Order;
import com.example.demo.model.entity.Place;
import com.example.demo.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdOrder(Long idOrder);
    List<Order> findByUser(String email);
    List<Order> findByPlace(Integer placeNumber);
    List<Order> findBySession(Session session);

    Optional<Order> findOrderBySessionAndPlace(Session session, Place place);


//    @Query("select order from Order order where order.session.sessionNumber = :sessionNumber and order.place.placeNumber = :placeNumber")
//    List<Order> getOrder(@Param("sessionNumber") String sessionNumber, @Param("placeNumber") Integer placeNumber); // поиск через HQL

//    @Query(value = "select * from orders where orders. = :place_id_place", nativeQuery = true)
//    List<Order> getOrderNative(@Param("name") String name); // поиск через SQL

}
