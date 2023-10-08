package flab.commercemarket.controller.user.dto;

import flab.commercemarket.domain.user.vo.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String picture;
    private Role role;
}
