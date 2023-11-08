package flab.commercemarket.service;

import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.repository.UserRepository;
import flab.commercemarket.domain.user.vo.Role;
import flab.commercemarket.domain.user.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    public void findUserTest() throws Exception {
        // given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> expectedPage = new PageImpl<>(Collections.emptyList());

        // when
        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        // then
        Page<User> result = userService.findUsers(page, size);
        verify(userRepository, times(1)).findAll(pageable); // findAll 메서드가 호출되었는지 확인
        assertThat(expectedPage).isEqualTo(result);
    }

    @Test
    public void getUserByIdTest() {
        // given
        long userId = 1L;
        User user = new User(); // 필요한 사용자 객체 생성

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // then
        User result = userService.getUserById(userId);
        assertThat(user).isEqualTo(result);
    }

    @Test
    public void deleteUserTest() {
        // given
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(1L);

        // then
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void changeUserRoleTest() {
        // given
        long userId = 1L;
        User user = User.builder().id(userId).role(Role.USER).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.changeUserRole(userId);

        // then
        verify(userRepository, times(1)).findById(userId);
        Assertions.assertEquals(Role.ADMIN, user.getRole());
    }
}
