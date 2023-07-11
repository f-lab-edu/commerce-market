package flab.commercemarket.user.repository;

import flab.commercemarket.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    // 내부 로직용
    Optional<User> findById(Long id);

    // 유저 찾기
    Optional<User> findByNameAndUsername(String name, String username);

    // 회원가입 시 닉네임으로 중복검사
    Optional<User> findByUsername(String username);

    List<User> findAll();

    User update(Long id, User user);

    void delete(Long id);
}
