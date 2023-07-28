package flab.commercemarket.controller.user;

import flab.commercemarket.controller.user.dto.UserDto;
import flab.commercemarket.controller.user.dto.UserResponseDto;
import flab.commercemarket.domain.user.UserService;
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

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.deleteOne(userId);
    }
}
