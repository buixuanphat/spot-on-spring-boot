package com.buixuanphat.spot_on.entity;

import com.buixuanphat.spot_on.enums.OrganizerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "location", nullable = false)
    private String location;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @Column(name = "age_limit", nullable = false)
    private Integer ageLimit;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @ColumnDefault("0")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "status", nullable = false)
    private String status = OrganizerStatus.pending.name();

    @Column(name = "license")
    private String license;

    @Column(name = "image_id")
    String imageId;

    @Column(name = "license_id")
    String licenseId;
}