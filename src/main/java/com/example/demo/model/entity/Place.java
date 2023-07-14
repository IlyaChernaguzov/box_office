package com.example.demo.model.entity;


import com.example.demo.model.enums.PlaceStatus;
import com.example.demo.model.enums.Booking;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "places")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Place {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idPlace;

//    @Column(name = "place_number")
//    Integer placeNumber;

    @Column(name = "row_number")
    Integer rowNumber;

    @Column(name = "place_number_in_row")
    Integer placeNumberInRow;

    @CreationTimestamp
    @JsonIgnore
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    PlaceStatus placeStatus = PlaceStatus.CREATE;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Order> order;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    Hall hall;

}
