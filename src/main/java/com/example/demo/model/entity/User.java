package com.example.demo.model.entity;

import com.example.demo.model.enums.PlaceStatus;
import com.example.demo.model.enums.UserStatus;
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
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idUser;

    @Column(name = "name_users")
    String name;

    @Column(name = "email", unique = true)
    String email;

    @CreationTimestamp
    @JsonIgnore
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    UserStatus userStatus = UserStatus.CREATE;

//    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Order> order;

}
