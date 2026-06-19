package com.peerisland.orders.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_address")
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    private String address;
    private String floor;
    private String district;
    private String street;
    private String apartment;
    private String landmark;
    private BigDecimal lat;
    private BigDecimal lng;
    private String buildingNameNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "add_date")
    private LocalDateTime addDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;

    private Boolean isDefault = false;

    private String addressType;

    public CustomerAddress(Long addressId) {
        this.addressId = addressId;
    }

    @Transient
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder(StringUtils.isNotBlank(address) ? address : StringUtils.EMPTY);
        if (StringUtils.isNotBlank(buildingNameNumber)) {
            fullAddress.append(", ").append(buildingNameNumber);
        }
        if (StringUtils.isNotBlank(floor)) {
            fullAddress.append(", Floor: ").append(floor);
        }
        return fullAddress.toString();
    }

}
