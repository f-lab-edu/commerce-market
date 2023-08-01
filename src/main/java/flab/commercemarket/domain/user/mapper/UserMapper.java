package flab.commercemarket.domain.user.mapper;

import flab.commercemarket.common.helper.Enum;
import flab.commercemarket.domain.user.vo.Authority;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.user.vo.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    int save(User user);

    int saveUserRole(UserRole userRole);

    int updateUserRole(@Param("userId") Long userId, @Param("authority") @Enum(enumClass = Authority.class, ignoreCase = true) int authority);

    // 내부 로직용
    Optional<User> findById(Long id);

    // 유저 찾기
    Optional<User> findByNameAndUsername(@Param("name") String name, @Param("username") String username);

    // 회원가입 시 닉네임으로 중복검사
    Optional<User> findByUsername(String username);

    List<User> findAll();

    Optional<UserRole> findRoleById(Long id);

    int update(@Param("id") Long id, @Param("user") User user);

    int delete(Long id);

    int deleteUserRole(Long userId);
}
