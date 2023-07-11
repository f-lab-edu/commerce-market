package flab.commercemarket.user.service;

import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.user.domain.User;
import flab.commercemarket.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    //    MemoryUserRepository 사용 시
//    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // 회원 가입
    public User join(User user) {
        validateDuplicateUser(user);
//        userRepository.save(user);
        int insertedRowCnt = userMapper.save(user);
        return user;
    }

    // 전체 회원 조회
    public List<User> findUsers() {
//        return userRepository.findAll();
        return userMapper.findAll();
    }

    // 유저 찾기
    public User getUser(String name, String username) {
//        return userRepository.findByNameAndUsername(name, username)
        return userMapper.findByNameAndUsername(name, username)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 유저입니다."));
    }

    // Test용 getUserById
    public User getUserById(Long id) {
//        return userRepository.findById(id)
        return userMapper.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 id의 유저가 없습니다."));
    }

    // 유저 업데이트
    public User updateOne(Long id, User userForUpdate) {
//        User updatedUser = userRepository.update(id, userForUpdate);
        User userBeforeUpdate = checkExistingUser(id);
        int updatedRowCnt = userMapper.update(id, userForUpdate);
        userForUpdate.setId(id);
        return userForUpdate;
    }

    // 유저 삭제
    public void deleteOne(Long id) {
//        userRepository.delete(id);
        int deletedRowCnt = userMapper.delete(id);
    }

    private void validateDuplicateUser(User user) {
//        userRepository.findByUsername(user.getUsername())
        userMapper.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    private User checkExistingUser(Long id) {
        Optional<User> userBeingVerified = userMapper.findById(id);
        return userBeingVerified.orElseThrow(() -> new DataNotFoundException("해당 id의 유저가 없음"));
    }

}
