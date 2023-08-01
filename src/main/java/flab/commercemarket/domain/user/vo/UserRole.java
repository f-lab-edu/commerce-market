package flab.commercemarket.domain.user.vo;

import flab.commercemarket.common.helper.Enum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UserRole {

    private Long id;
    private Long userId;

    @Enum(enumClass = Authority.class, ignoreCase = true)
    private Integer authority;

    public UserRole(Long userId, Integer authority) {
        this.userId = userId;
        this.authority = authority;
    }
}
