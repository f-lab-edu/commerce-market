package flab.commercemarket.user.service;

import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.user.domain.User;
import flab.commercemarket.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    public User join(User user) {
        log.info("Start join User");

        validateDuplicateUser(user);
        int insertedRowCnt = userMapper.save(user);

        log.info("Create user. {}", user);
        return user;
    }

    public List<User> findUsers() {
        log.info("Start findUsers");

        List<User> foundUsers = userMapper.findAll();

        log.info("FindUsers. {}", foundUsers);
        return foundUsers;
    }

    public User getUser(String name, String username) {
        log.info("Start getUserByNameAndUsername");

        return userMapper.findByNameAndUsername(name, username)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 유저입니다."));
    }

    public User getUserById(Long id) {
        log.info("Start getUserById");

        return userMapper.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 id의 유저가 없습니다."));
    }

    public User updateOne(Long id, User userForUpdate) {
        log.info("Start update User");

        User userBeforeUpdate = checkExistingUser(id);
        int updatedRowCnt = userMapper.update(id, userForUpdate);
        userForUpdate.setId(id);

        log.info("Update User. {}", userForUpdate);
        return userForUpdate;
    }

    public void deleteOne(Long id) {
        log.info("Start delete User");

        int deletedRowCnt = userMapper.delete(id);
        log.info("Delete User. userId = {}", id);
    }

    private void validateDuplicateUser(User user) {
        log.info("Start validateDuplicate User");

        userMapper.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });

        log.info("Duplicate check done. username = {}", user.getUsername());
    }

    private User checkExistingUser(Long id) {
        log.info("Start checkExistingUser");

        Optional<User> userBeingVerified = userMapper.findById(id);
        return userBeingVerified.orElseThrow(() -> new DataNotFoundException("해당 id의 유저가 없음"));
    }

}
