package com.peerisland.orders.mapper;

import com.peerisland.orders.dto.customer.RoleDto;
import com.peerisland.orders.entity.Roles;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toRoleDto(Roles roles);

    List<RoleDto> toRoleDtoList(List<Roles> rolesList);
}