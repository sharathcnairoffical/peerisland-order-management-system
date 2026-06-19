package com.peerisland.orders.mapper;

import com.peerisland.orders.dto.customer.CustomerAddressDto;
import com.peerisland.orders.entity.CustomerAddress;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerAddressMapper {

    @Mapping(target = "customerId" , source = "customer.customerId")
    CustomerAddressDto toDto(CustomerAddress customerAddress);

    List<CustomerAddressDto> toDtoList(List<CustomerAddress> customerAddresses);

}
