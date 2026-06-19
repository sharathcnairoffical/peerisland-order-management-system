package com.peerisland.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long orderDetailId;

    private String orderUniqueId;
    private Integer orderType;
    private String imageUrl;
    private Long productId;
    private String name;
    private Integer quantity;
    private Double productPrice;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @Builder.Default
    private LocalDateTime addDate = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime modDate = LocalDateTime.now();

    private String addBy;

    private String modBy;
}
