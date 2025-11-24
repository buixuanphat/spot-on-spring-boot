package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "limitTicket", nullable = false)
    private Integer limitTicket;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @Column(name = "totalSeats", nullable = false)
    private Integer totalSeats;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventId", nullable = false)
    private Event event;


}