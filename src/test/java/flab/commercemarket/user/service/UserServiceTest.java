package flab.commercemarket.user.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.mapper.UserMapper;
import flab.commercemarket.domain.user.vo.Authority;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.user.vo.UserRole;
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

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 가입")
    void register() {
        //given
        User user = makeUserFixture(1);
        user.setId(1L);

        //when
        User joinedUser = makeJoinedUserFixture(1);

        //then
        assertThat(joinedUser).isEqualTo(user);

        verify(userMapper).save(user);
    }

    @Test()
    @DisplayName("중복 회원가입 시 에러 발생")
    void registerDuplicateUser() {
        //given
        makeJoinedUserFixture(1);
        User user2 = makeUserFixture(1);

        //when

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
        User joinedUser = makeJoinedUserFixture(1);
        Long joinedUserId = joinedUser.getId();

        //when
        User userForUpdate = makeUserFixture(2);
        userForUpdate.setId(1L);

        when(userMapper.findById(joinedUserId))
                .thenReturn(Optional.ofNullable(joinedUser));
        User updatedUser = userService.updateOne(joinedUserId, userForUpdate);

        //then
        assertThat(updatedUser).isEqualTo(userForUpdate);
    }

    @Test
    @DisplayName("유저 권한 수정(default BUYER => SELLER 변경)")
    void updateUserRole() {
        //given
        User user = makeJoinedUserFixture(1);

        //when
        String authority = "SELLER";
        when(userMapper.updateUserRole(user.getId(), Authority.valueOf(authority).getAuthorityValue()))
                .thenReturn(1);
        userService.updateUserRole(1L, authority);

        //then
        UserRole userRole = new UserRole(user.getId(), Authority.SELLER.getAuthorityValue());
        when(userMapper.findRoleById(1L)).thenReturn(Optional.ofNullable(userRole));
        Authority foundAuthority = userService.getUserRoleById(1L);

        assertThat(foundAuthority.name()).isEqualTo(authority);
        verify(userMapper).updateUserRole(user.getId(), Authority.valueOf(authority).getAuthorityValue());
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
    @DisplayName("유저 권한 찾기")
    void findUserRole() {
        //given
        User user = makeJoinedUserFixture(1);

        //when
        UserRole userRole = new UserRole(user.getId(), Authority.BUYER.getAuthorityValue());
        when(userMapper.findRoleById(1L)).thenReturn(Optional.ofNullable(userRole));
        Authority foundAuthority = userService.getUserRoleById(1L);

        //then
        assertThat(foundAuthority).isEqualTo(Authority.BUYER);
    }

    @Test
    @DisplayName("유저 찾기")
    void findUser() {
        //given
        User joinedUser1 = makeJoinedUserFixture(1);
        User joinedUser2 = makeJoinedUserFixture(2);

        //when
        when(userMapper.findByNameAndUsername(
                joinedUser1.getName(), joinedUser1.getUsername()
        )).thenReturn(Optional.ofNullable(joinedUser1));

        //then
        User foundUser = userService.getUser(
                joinedUser1.getName(), joinedUser1.getUsername());

        assertThat(foundUser).isEqualTo(joinedUser1);
    }

    @Test
    @DisplayName("유저 찾기 실패")
    void findUserFail() {
        //given
        User joinedUser1 = makeJoinedUserFixture(1);
        User joinedUser2 = makeJoinedUserFixture(2);

        //when
        when(userMapper.findByNameAndUsername(
                "nonExistentUser", joinedUser1.getUsername()
        )).thenReturn(Optional.empty());

        //then
        DataNotFoundException e = assertThrows(DataNotFoundException.class,
                () -> userService.getUser(
                        "nonExistentUser", joinedUser1.getUsername()));

        assertThat(e.getMessage()).isEqualTo("존재하지 않는 유저입니다.");
    }

    @Test
    @DisplayName("유저 삭제")
    void delete() {
        //given
        User joinedUser = makeJoinedUserFixture(1);

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

    User makeJoinedUserFixture(int param) {
        User user1 = makeUserFixture(param);
        when(userMapper.save(user1)).thenReturn(1);
        user1.setId(1L);
        UserRole userRole = new UserRole(user1.getId(), Authority.BUYER.getAuthorityValue());
        when(userMapper.saveUserRole(userRole)).thenReturn(1);
        when(userMapper.findByNameAndUsername(user1.getName(), user1.getUsername())).thenReturn(Optional.of(user1));
        User joinedUser = userService.join(user1);

        return joinedUser;
    }
}