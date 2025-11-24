package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;

@Entity
public class VoucherEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventId", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voucherId", nullable = false)
    private Voucher voucher;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

}