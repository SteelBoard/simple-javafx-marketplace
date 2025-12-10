package org.steelboard.marketplace.mapper;

import org.springframework.stereotype.Component;
import org.steelboard.marketplace.dto.user.UserRegisterDto;
import org.steelboard.marketplace.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());

        return user;
    }
}
