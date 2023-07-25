package flab.commercemarket.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.user.domain.User;
import flab.commercemarket.user.dto.UserDto;
import flab.commercemarket.user.dto.UserResponseDto;
import flab.commercemarket.user.service.UserService;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("회원가입 API 테스트")
    void signUp() throws Exception {
        //given
        UserDto userDto = makeUserDtoFixture(1);
        User user = userDto.toUser();
        user.setId(1L);
        UserResponseDto userResponseDto = user.toUserResponseDto();

        doReturn(user).when(userService).join(any(User.class));

        //when
        String jsonDto = objectMapper.writeValueAsString(userDto);
        ResultActions perform = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userResponseDto.getUsername()))
                .andExpect(jsonPath("$.password").value(userResponseDto.getPassword()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.address").value(userResponseDto.getAddress()))
                .andExpect(jsonPath("$.phoneNumber").value(userResponseDto.getPhoneNumber()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));
    }

    @Test
    @DisplayName("유저 찾기 API 테스트")
    void getUser() throws Exception {
        //given
        User user = makeUserFixture(1);
        UserResponseDto userResponseDto = user.toUserResponseDto();

        doReturn(user).when(userService)
                .getUser(eq(user.getName()), eq(user.getUsername()));

        //when
        ResultActions perform = mockMvc.perform(
                get("/users/find/{name}/{username}", user.getName(), user.getUsername()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.username").value(userResponseDto.getUsername()))
                .andExpect(jsonPath("$.password").value(userResponseDto.getPassword()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.address").value(userResponseDto.getAddress()))
                .andExpect(jsonPath("$.phoneNumber").value(userResponseDto.getPhoneNumber()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));
    }

    @Test
    @DisplayName("모든 유저 리스트 반환 API 테스트")
    void getUserList() throws Exception {
        //given
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(makeUserFixture(i + 1));
        }
        doReturn(userList).when(userService).findUsers();

        //when
        ResultActions perform = mockMvc.perform(get("/users"));

        //then
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(contentAsString);
        JSONArray jsonArray = (JSONArray) obj;
        assertThat(jsonArray.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("유저 업데이트 API 테스트")
    void updateUser() throws Exception {
        //given
        UserDto userDto = makeUserDtoFixture(1);
        User user = userDto.toUser();
        Long userId = 1L;
        user.setId(userId);
        UserResponseDto userResponseDto = user.toUserResponseDto();

        doReturn(user).when(userService).updateOne(
                eq(userId), any(User.class));

        //when
        String jsonDto = objectMapper.writeValueAsString(userDto);
        ResultActions perform = mockMvc.perform(patch("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDto));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.username").value(userResponseDto.getUsername()))
                .andExpect(jsonPath("$.password").value(userResponseDto.getPassword()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.address").value(userResponseDto.getAddress()))
                .andExpect(jsonPath("$.phoneNumber").value(userResponseDto.getPhoneNumber()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));
    }

    @Test
    @DisplayName("유저 삭제 API 테스트")
    void deleteUser() throws Exception {
        //given
        Long userId = 1L;

        //when
        ResultActions perform = mockMvc.perform(
                delete("/users/{userId}", userId));

        //then
        perform.andExpect(status().isOk());
        verify(userService).deleteOne(userId);
    }


    User makeUserFixture(int param) {
        return User.builder()
                .id((long) param)
                .username("user" + param)
                .password("pass" + param)
                .name("김" + param)
                .email("email" + param)
                .phoneNumber("phone" + param)
                .address("address" + param)
                .build();
    }

    UserDto makeUserDtoFixture(int param) {
        return UserDto.builder()
                .username("user" + param)
                .password("pass" + param)
                .name("김" + param)
                .email("email" + param)
                .phoneNumber("phone" + param)
                .address("address" + param)
                .build();
    }

}