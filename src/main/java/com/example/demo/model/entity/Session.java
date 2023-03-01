package com.example.demo.model.entity;

import com.example.demo.model.enums.PlaceStatus;
import com.example.demo.model.enums.SessionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sessions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idSession;

    @Column(name = "start_session")
    LocalDateTime startSession;

    @Column(name = "price")
    Integer price;

    @CreationTimestamp
    @JsonIgnore
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    SessionStatus sessionStatus = SessionStatus.CREATE;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    Cinema cinema;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    Hall hall;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    Movie movie;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Order> order;
}
