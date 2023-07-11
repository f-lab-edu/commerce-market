package flab.commercemarket.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;

    public UserResponseDto(String username, String password, String name, String address, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
