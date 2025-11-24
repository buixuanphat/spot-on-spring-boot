package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "dateOfBirth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "avatar")
    private String avatar;

    @ColumnDefault("0")
    @Column(name = "coins")
    private Integer coins;

    @ColumnDefault("'copper'")
    @Lob
    @Column(name = "tier")
    private String tier;

    @ColumnDefault("'customer'")
    @Lob
    @Column(name = "role", nullable = false)
    private String role;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdDate", nullable = false)
    private Instant createdDate = Instant.now();

    @ColumnDefault("1")
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "avatar_id", nullable = true)
    private String avatarId;




}