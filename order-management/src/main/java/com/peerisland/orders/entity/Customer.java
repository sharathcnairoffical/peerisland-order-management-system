package com.peerisland.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "mobile_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    private String countryCode;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(unique = true)
    private String customerUniqueId;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    private String status;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Orders> orders;

    @Column(name = "add_by")
    private String addBy;

    @Column(name = "mod_by")
    private String modBy;

    @Column(name = "add_date")
    private LocalDateTime addDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;
}
