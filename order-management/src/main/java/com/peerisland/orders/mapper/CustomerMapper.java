package com.peerisland.orders.mapper;

import com.peerisland.orders.dto.customer.CustomerDto;
import com.peerisland.orders.entity.Customer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        uses = {RoleMapper.class})
public interface CustomerMapper {

    CustomerDto toCustomerDto(Customer customer);

    List<CustomerDto> toCustomerDtoList(List<Customer> customer);
}
