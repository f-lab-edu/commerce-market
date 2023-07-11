package flab.commercemarket.user.dto;

import flab.commercemarket.user.domain.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String username;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;

    public User toUser() {
        return User.builder()
                .password(password)
                .name(name)
                .username(username)
                .phoneNumber(phoneNumber)
                .address(address)
                .email(email)
                .build();
    }
}
