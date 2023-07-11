package flab.commercemarket.user.service;

import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.user.domain.User;
import flab.commercemarket.user.repository.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

//    @Spy
//    MemoryUserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 가입")
    void register() {
        //given
        User user = makeUserFixture(1);

        //when
        User savedUser = userService.join(user);

        //then
        assertThat(savedUser).isEqualTo(user);

        verify(userMapper).save(user);
    }

    @Test()
    @DisplayName("중복 회원가입 시 에러 발생")
    void registerDuplicateUser() {
        //given
        User user1 = makeUserFixture(1);
        User user2 = makeUserFixture(1);

        //when
        userService.join(user1);
        when(userMapper.save(user2)).thenThrow(new IllegalStateException("이미 존재하는 회원입니다."));

        //then
        IllegalStateException e = assertThrows(
                IllegalStateException.class, () -> userService.join(user2)
        );

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }

    @Test
    @DisplayName("유저 정보 수정")
    void update() {
        //given
        User joinedUser = userService.join(makeUserFixture(1));
        Long joinedUserId = joinedUser.getId();
        User userForUpdate = makeUserFixture(2);
        userForUpdate.setId(1L);

        //when
        when(userMapper.findById(joinedUserId))
                .thenReturn(Optional.ofNullable(joinedUser));
        User updatedUser = userService.updateOne(joinedUserId, userForUpdate);

        //then
        assertThat(updatedUser).isEqualTo(userForUpdate);
    }

    @Test
    @DisplayName("존재하지 않는 유저의 id에 업데이트 시도")
    void updateFail() {
        //given
        User userForUpdate = makeUserFixture(1);
        Long userId = 1L;

        //when
        when(userMapper.findById(userId))
                .thenReturn(Optional.empty());
        //then
        DataNotFoundException e = assertThrows(DataNotFoundException.class,
                () -> userService.updateOne(userId, userForUpdate));

        assertThat(e.getMessage()).isEqualTo("해당 id의 유저가 없음");
    }

    @Test
    @DisplayName("유저 찾기")
    void findUser() {
        //given
        User user1 = makeUserFixture(1);
        User user2 = makeUserFixture(2);
        User joinedUser = userService.join(user1);
        userService.join(user2);

        //when
        when(userMapper.findByNameAndUsername(
                user1.getName(), user1.getUsername()
        )).thenReturn(Optional.ofNullable(joinedUser));

        //then
        User foundUser = userService.getUser(
                user1.getName(), user1.getUsername());

        assertThat(foundUser).isEqualTo(joinedUser);
    }

    @Test
    @DisplayName("유저 찾기 실패")
    void findUserFail() {
        //given
        User user1 = makeUserFixture(1);
        User user2 = makeUserFixture(2);
        User joinedUser = userService.join(user1);
        userService.join(user2);

        //when
        when(userMapper.findByNameAndUsername(
                "nonExistentUser", user1.getUsername()
        )).thenReturn(Optional.empty());

        //then
        DataNotFoundException e = assertThrows(DataNotFoundException.class,
                () -> userService.getUser(
                        "nonExistentUser", user1.getUsername()));

        assertThat(e.getMessage()).isEqualTo("존재하지 않는 유저입니다.");
    }

    @Test
    @DisplayName("유저 삭제")
    void delete() {
        //given
        User user = makeUserFixture(1);
        User joinedUser = userService.join(user);

        //when
        userService.deleteOne(joinedUser.getId());

        //then 삭제 완료로 검색실패 됨을 검증
        DataNotFoundException e = assertThrows(DataNotFoundException.class,
                () -> userService.getUserById(joinedUser.getId()));

        assertThat(e.getMessage()).isEqualTo("해당 id의 유저가 없습니다.");
    }

    User makeUserFixture(int param) {
        return User.builder()
                .username("user" + param)
                .password("pass" + param)
                .name("김" + param)
                .email("email" + param)
                .phoneNumber("phone" + param)
                .address("address" + param)
                .build();
    }
}