package flab.commercemarket.controller.user;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.user.dto.UserDto;
import flab.commercemarket.controller.user.dto.UserResponseDto;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/{userId}")
    public UserResponseDto getOne(
            @PathVariable long userId) {
        User foundUser = userService.getUserById(userId);
        return foundUser.toUserResponseDto();
    }

    @GetMapping
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

    @PatchMapping("/{userId}")
    public UserResponseDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        User userForUpdate = userDto.toUser();
        User updatedUser = userService.updateOne(userId, userForUpdate);
        return updatedUser.toUserResponseDto();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.deleteOne(userId);
    }
}
