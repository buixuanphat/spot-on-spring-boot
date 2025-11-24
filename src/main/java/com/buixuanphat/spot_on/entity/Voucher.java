package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "effectiveDate", nullable = false)
    private Instant effectiveDate;

    @Column(name = "expirationDate", nullable = false)
    private Instant expirationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizerId", nullable = false)
    private Organizer organizer;

    @Column(name = "limitUsed")
    private Integer limitUsed;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "value", nullable = false)
    private Double value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Instant effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public Integer getLimitUsed() {
        return limitUsed;
    }

    public void setLimitUsed(Integer limitUsed) {
        this.limitUsed = limitUsed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}