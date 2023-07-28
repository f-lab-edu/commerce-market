package flab.commercemarket.domain.user.mapper;

import flab.commercemarket.domain.user.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    int save(User user);

    // 내부 로직용
    Optional<User> findById(Long id);

    // 유저 찾기
    Optional<User> findByNameAndUsername(@Param("name") String name, @Param("username") String username);

    // 회원가입 시 닉네임으로 중복검사
    Optional<User> findByUsername(String username);

    List<User> findAll();

    int update(@Param("id") Long id, @Param("user") User user);

    int delete(Long id);
}
