package com.peerisland.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roles {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "role_id")
        private Long roleId;

        @Column(name = "role_name", unique = true, nullable = false)
        private String roleName;

        @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
        private List<Customer> customers;
}
