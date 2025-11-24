package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;

@Entity
public class EventMerchandise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventId", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchandiseId", nullable = false)
    private Merchandise merchandise;

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

    public Merchandise getMerchandise() {
        return merchandise;
    }

    public void setMerchandise(Merchandise merchandise) {
        this.merchandise = merchandise;
    }

}