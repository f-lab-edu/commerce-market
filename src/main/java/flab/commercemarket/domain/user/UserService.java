package flab.commercemarket.domain.user;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.domain.user.repository.UserRepository;
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

    @Transactional
    public User join(User user) {
        log.info("Start join User");

        validateDuplicateUser(user.getUsername());
        User createdUser = userRepository.save(user);

        log.info("Create user. {}", user.getUsername());
        return createdUser;
    }

    @Transactional(readOnly = true)
    public Page<User> findUsers(int page, int size) {
        log.info("Start findUsers");

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> foundUserList = userRepository.findAll(pageable);

        log.info("FoundUsers. {}", foundUserList);
        return foundUserList;
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        log.info("Start getUserById");

        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 id의 유저가 없습니다."));
    }

    @Transactional
    public User updateOne(long userId, User userForUpdate) {
        log.info("Start update User");

        User foundUser = getUserById(userId);

        foundUser.setUsername(userForUpdate.getUsername());
        foundUser.setPassword(userForUpdate.getPassword());
        foundUser.setName(userForUpdate.getName());
        foundUser.setAddress(userForUpdate.getAddress());
        foundUser.setPhoneNumber(userForUpdate.getPhoneNumber());
        foundUser.setEmail(userForUpdate.getEmail());

        return foundUser;
    }

    @Transactional
    public void deleteOne(long id) {
        log.info("Start delete User");

        User foundUser = getUserById(id);
        userRepository.delete(foundUser);
        log.info("Delete User. userId = {}", foundUser.getId());
    }

    private void validateDuplicateUser(String username) {
        log.info("Start validateDuplicate User");

        boolean result = userRepository.isAlreadyExistUser(username);
        if (result) {
            throw new DuplicateDataException("이미 존재하는 사용자");
        }
    }

}
