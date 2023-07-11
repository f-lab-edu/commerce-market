package flab.commercemarket.user.domain;

import flab.commercemarket.user.dto.UserResponseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class User {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;

    public User(String username, String password, String name, String address, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public UserResponseDto toUserResponseDto() {
        return UserResponseDto.builder()
                .id(id)
                .username(username)
                .password(password)
                .name(name)
                .address(address)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
    }
}
