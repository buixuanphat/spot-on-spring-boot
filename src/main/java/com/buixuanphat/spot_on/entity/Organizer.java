package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Data
public class Organizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "taxCode", nullable = false, length = 50)
    private String taxCode;

    @Column(name = "bankNumber", nullable = false, length = 50)
    private String bankNumber;

    @Column(name = "bank", nullable = false, length = 100)
    private String bank;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "phoneNumber", nullable = false, length = 10)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "businessLicense")
    private String businessLicense;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdDate", nullable = false)
    private Instant createdDate;

    @ColumnDefault("0")
    @Column(name = "active", nullable = false)
    private Boolean active = false;

    @ColumnDefault("1")
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "avatar_id", nullable = true)
    private String avatarId;

    @Column(name = "business_license_id", nullable = true)
    private String businessLicenseId;
}