package com.example.demo.model.entity;

import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.MovieStatus;
import com.example.demo.model.enums.Rating;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "movies")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idMovie;

    @Column(name = "name_movie", unique = true)
    String nameMovie;

    @Column(name = "duration_movie")
    Integer durationMovie;

    @Column(name = "start_movie")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startRental;

    @Column(name = "end_movie")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endRental;

    @Column(name = "rating_movie")
    @Enumerated(EnumType.STRING)
    Rating ratingMovie;

    @Column(name = "genre_movie")
    @Enumerated(EnumType.STRING)
    Genre genreMovie;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    MovieStatus movieStatus = MovieStatus.CREATE;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    List<Session> session;

}
