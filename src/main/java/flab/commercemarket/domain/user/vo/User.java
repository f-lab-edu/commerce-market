package flab.commercemarket.domain.user.vo;

import flab.commercemarket.controller.user.dto.UserResponseDto;
import flab.commercemarket.domain.product.vo.Product;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "market_user")
@Table(indexes = {
        @Index(name = "idx_name", columnList = "name")
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

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "seller")
    private List<Product> productList;

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
