package com.peerisland.orders.mapper;

import com.peerisland.orders.dto.order.OrderDetailsDto;
import com.peerisland.orders.entity.OrderDetails;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface OrderDetailsMapper {


    OrderDetailsDto toOrderDetailsDto(OrderDetails orderDetails);
    List<OrderDetailsDto> orderDetailsDtoList (List<OrderDetails> orderDetails);

    default double resolveProductPrice(OrderDetails orderDetails) {
        return orderDetails.getProductPrice() == null ? 0.0 :  orderDetails.getProductPrice();
    }
}
