package flab.commercemarket.controller.user;

import flab.commercemarket.controller.user.dto.UserDto;
import flab.commercemarket.controller.user.dto.UserResponseDto;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.Authority;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDto create(@RequestBody UserDto userDto) {
        User user = userDto.toUser();

        User joinedUser = userService.join(user);
        return joinedUser.toUserResponseDto();
    }

    @GetMapping("/find/{name}/{username}")
    public UserResponseDto getOne(
            @PathVariable String name,
            @PathVariable String username) {
        User foundUser = userService.getUser(name, username);
        return foundUser.toUserResponseDto();
    }

    //  로그인 된 유저의 권한 가져오기(Todo)
    @GetMapping("/role")
    public String getUserRole() {
        Long userId = 1L;
        Authority authorityById = userService.getUserRoleById(userId);
        return authorityById.name();
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        List<User> users = userService.findUsers();
        return users.stream()
                .map(User::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserResponseDto update(
            @PathVariable Long userId,
            @RequestBody UserDto userDto
    ) {
        User userForUpdate = userDto.toUser();
        User upatedUser = userService.updateOne(userId, userForUpdate);
        return upatedUser.toUserResponseDto();
    }

    @PatchMapping("/role/{userId}")
    public String updateUserRole(
            @PathVariable Long userId,
            @RequestBody String authority
    ) {
        userService.updateUserRole(userId, authority);
        return "ok";
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.deleteOne(userId);
    }
}
