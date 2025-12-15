package com.buixuanphat.spot_on.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId")
    Voucher voucher;

    @Column(name = "purchase_time")
    Instant purchaseTime;

    @Column(name = "status")
    String status;

    @Column(name = "total_payment")
    Double totalPayment;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdDate", nullable = false)
    Instant createdDate;

}