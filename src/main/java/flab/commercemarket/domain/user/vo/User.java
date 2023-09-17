package flab.commercemarket.domain.user.vo;

import flab.commercemarket.controller.user.dto.UserResponseDto;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "market_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;

    @Builder
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
