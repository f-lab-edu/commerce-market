package flab.commercemarket.controller.user;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.user.dto.UserResponseDto;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    public UserResponseDto getUser(@PathVariable long userId) {
        User foundUser = userService.getUserById(userId);
        return foundUser.toUserResponseDto();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    public PageResponseDto<UserResponseDto> getAll(@RequestParam int page, @RequestParam int size) {
        Page<User> usersPage = userService.findUsers(page, size);

        List<UserResponseDto> content = usersPage.stream()
                .map(User::toUserResponseDto)
                .collect(Collectors.toList());

        return PageResponseDto.<UserResponseDto>builder()
                .page(page)
                .size(size)
                .totalElements(usersPage.getTotalElements())
                .content(content)
                .build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public void userRole(@PathVariable long userId) {
        userService.changeUserRole(userId);
    }
}
