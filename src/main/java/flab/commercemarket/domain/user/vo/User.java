package flab.commercemarket.domain.user.vo;

import flab.commercemarket.controller.user.dto.UserResponseDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "market_user")
@Table(indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_user_id", columnList = "id")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private Role role;

    public String getRoleKey() {
        return role.getKey();
    }

    public UserResponseDto toUserResponseDto() {
        return UserResponseDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .picture(picture)
                .role(role)
                .build();
    }
}
