package flab.commercemarket.user.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.repository.UserRepository;
import flab.commercemarket.domain.user.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입")
    public void joinTest() throws Exception {
        // given
        User user = User.builder()
                .username("username1")
                .address("서울")
                .phoneNumber("010-0000-0000")
                .build();

        when(userRepository.save(any())).thenReturn(user);

        // when
        User expectedUser = userService.join(user);

        // then
        assertThat(user.getUsername()).isEqualTo(expectedUser.getUsername());
        assertThat(user.getAddress()).isEqualTo(expectedUser.getAddress());
        assertThat(user.getPhoneNumber()).isEqualTo(expectedUser.getPhoneNumber());
    }

    @Test()
    @DisplayName("중복 회원가입 시 에러 발생")
    void registerDuplicateUser() {
        //given
        User user = User.builder()
                .username("username1")
                .address("서울")
                .phoneNumber("010-0000-0000")
                .build();

        //when
        when(userRepository.isAlreadyExistUser(user.getUsername()))
                .thenThrow(DuplicateDataException.class);

        //then
        assertThrows(DuplicateDataException.class, () -> userService.join(user));
    }

    @Test
    public void getUserByIdTest() throws Exception {
        // given
        long userId = 100L;
        User user = User.builder()
                .username("username1")
                .address("서울")
                .phoneNumber("010-0000-0000")
                .build();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User foundUser = userService.getUserById(userId);

        // then
        assertThat(foundUser.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
    }

    @Test
    void updateUserTest() {
        // Given
        long userId = 1L;

        User userForUpdate = User.builder()
                .username("newUsername")
                .password("newPassword")
                .name("newName")
                .build();

        User foundUser = User.builder()
                .username("oldUsername")
                .password("oldPassword")
                .name("oldName")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(foundUser));
        when(userRepository.save(any(User.class))).thenReturn(foundUser);

        // when
        User updatedUser = userService.updateOne(userId, userForUpdate);

        assertThat(foundUser.getUsername()).isEqualTo(updatedUser.getUsername());
        assertThat(foundUser.getPassword()).isEqualTo(updatedUser.getPassword());
        assertThat(foundUser.getName()).isEqualTo(updatedUser.getName());
    }

    @Test
    @DisplayName("존재하지 않는 유저의 id에 업데이트 시도")
    void updateFail() {
        //given
        long userId = 1L;
        User user = User.builder()
                .username("username1")
                .address("서울")
                .phoneNumber("010-0000-0000")
                .build();
        user.setId(userId);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //then
        assertThrows(DataNotFoundException.class, () -> {
            userService.updateOne(userId, user);
        });
    }

    @Test
    @DisplayName("유저 삭제")
    void delete() {
        // given
        long userId = 1L;
        User foundUser = new User();
        foundUser.setId(userId);
        foundUser.setUsername("testUser");
        foundUser.setPassword("testPassword");
        foundUser.setName("Test User");
        foundUser.setAddress("Test Address");
        foundUser.setPhoneNumber("1234567890");
        foundUser.setEmail("test@example.com");

        doNothing().when(userRepository).delete(foundUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(foundUser));

        // when
        userService.deleteOne(userId);

        // then
        verify(userRepository, times(1)).delete(foundUser);
    }
}