package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sectionId", nullable = false)
    private Section section;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdDate", nullable = false)
    private Instant createdDate;

    @ColumnDefault("'available'")
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "purchaseTime")
    private Instant purchaseTime;

    @Column(name = "qrCode")
    private String qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceId")
    private Invoice invoice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Instant purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

}