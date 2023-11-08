package flab.commercemarket.domain.user;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.domain.user.repository.UserRepository;
import flab.commercemarket.domain.user.vo.Role;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> findUsers(int page, int size) {
        log.info("Start findUsers");

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> foundUserList = userRepository.findAll(pageable);

        log.info("FoundUsers. {}", foundUserList);
        return foundUserList;
    }

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        log.info("Start getUserById");

        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("해당 id의 유저가 없습니다."));
    }

    @Transactional
    public void deleteUser(long userId) {
        log.info("Start delete User");

        User foundUser = getUserById(userId);
        userRepository.delete(foundUser);
        log.info("Delete User. userId = {}", foundUser.getId());
    }

    @Transactional
    public void changeUserRole(long userId) {
        User foundUser = getUserById(userId);
        foundUser.setRole(foundUser.getRole() == Role.USER ? Role.ADMIN : Role.USER);
    }
}
