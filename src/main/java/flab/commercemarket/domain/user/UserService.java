package flab.commercemarket.domain.user;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.domain.user.mapper.UserMapper;
import flab.commercemarket.domain.user.vo.Authority;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.user.vo.UserRole;
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
        if (insertedRowCnt != 1) {
            log.error("Fail Create user. user = {}", user);
            throw new RuntimeException("회원 데이터 삽입에 실패하였습니다.");
        }
        User joinedUser = getUser(user.getName(), user.getUsername());

        // 권한 추가(default : BUYER)
        UserRole userRole = new UserRole(joinedUser.getId(), Authority.BUYER.getAuthorityValue());
        int insertedRowCnt2 = userMapper.saveUserRole(userRole);
        if (insertedRowCnt2 != 1) {
            log.error("Fail Create user role. user = {}", user);
            throw new RuntimeException("회원 권한 데이터 삽입에 실패하였습니다.");
        }


        log.info("Create user. user = {}", user);
        return joinedUser;
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

    public Authority getUserRoleById(Long id) {
        log.info("Start getUserRoleById");

        return userMapper.findRoleById(id)
                .map(UserRole::getAuthority)
                .map(Authority::valueOf)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 유저입니다."));
    }

    public User updateOne(Long id, User userForUpdate) {
        log.info("Start update User");

        User userBeforeUpdate = checkExistingUser(id);
        int updatedRowCnt = userMapper.update(id, userForUpdate);
        userForUpdate.setId(id);

        log.info("Update User. {}", userForUpdate);
        return userForUpdate;
    }

    public void updateUserRole(Long id, String role) {
        log.info("Start update UserRole");

        int updatedRowCnt = userMapper.updateUserRole(id, Authority.valueOf(role).getAuthorityValue());
        if (updatedRowCnt != 1) {
            log.error("Fail Update user role. userId = {}", id);
            throw new RuntimeException("회원 권한 데이터 갱신에 실패하였습니다.");
        }

        log.info("User role updated. userId = {}, updated role = {}", id, role);
    }

    public void deleteOne(Long id) {
        log.info("Start delete UserRole");

        int deletedRoleRowCnt = userMapper.deleteUserRole(id);

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
