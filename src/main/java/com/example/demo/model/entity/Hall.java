package com.example.demo.model.entity;

import com.example.demo.model.enums.HallStatus;
import com.example.demo.model.enums.PlaceStatus;
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
@Table(name = "halls")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hall {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "number_hall")
    Integer numberHall;

    @Column(name = "quantity_places")
    Integer places;

    @Column(name = "quantity_rows")
    Integer rows;

    @CreationTimestamp
    @JsonIgnore
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    HallStatus hallStatus = HallStatus.CREATE;

//    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Session> session;

//    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Place> place;

//    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    Cinema cinema;
}
