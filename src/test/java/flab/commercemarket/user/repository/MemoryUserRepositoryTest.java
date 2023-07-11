package flab.commercemarket.user.repository;

import flab.commercemarket.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class MemoryUserRepositoryTest {

//    Todo : DB 연결 후 변경

    private MemoryUserRepository repository = new MemoryUserRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    @DisplayName("유저 추가")
    void save() {
        //given
        User user = makeUserFixture(1);

        //when
        repository.save(user);

        //then
        User foundUser = repository.findById(user.getId()).get();
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("내부 로직 목적, id로 유저 찾기")
    void findById() {
        //given
        User user1 = makeUserFixture(1);
        repository.save(user1);

        User user2 = makeUserFixture(2);
        repository.save(user2);

        //when
        User foundUser1 = repository.findById(1L).get();
        User foundUser2 = repository.findById(2L).get();

        //then
        assertThat(foundUser1).isEqualTo(user1);
        assertThat(foundUser2).isEqualTo(user2);
    }

    @Test
    @DisplayName("모든 유저 찾기")
    void findAll() {
        //given
        User user1 = makeUserFixture(1);
        repository.save(user1);

        User user2 = makeUserFixture(2);
        repository.save(user2);

        User user3 = makeUserFixture(3);
        repository.save(user3);

        //when
        List<User> users = repository.findAll();

        //then
        assertThat(users.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("유저 업데이트")
    void update() {
        //given
        User user1 = makeUserFixture(1);
        User savedUser = repository.save(user1);
        User userForUpdate = makeUserFixture(2);
        userForUpdate.setId(1L);

        //when
        User updatedUser = repository.update(user1.getId(), userForUpdate);

        //then
        assertThat(updatedUser).isEqualTo(userForUpdate);
    }

    @Test
    @DisplayName("유저 찾기 ByNameAndUsername")
    void findOne() {
        //given
        User user1 = makeUserFixture(1);
        User user2 = makeUserFixture(2);
        User savedUser = repository.save(user1);
        repository.save(user2);

        //when
        User foundUser = repository.findByNameAndUsername(
                user1.getName(), user1.getUsername()).get();

        //then
        assertThat(foundUser).isEqualTo(savedUser);
    }

    @Test
    @DisplayName("유저 삭제")
    void delete() {
        //given
        User user1 = makeUserFixture(1);
        User user2 = makeUserFixture(2);
        User savedUser = repository.save(user1);

        //when
        repository.delete(savedUser.getId());

        //then
        Optional<User> deletedUser = repository.findById(savedUser.getId());
        assertThat(deletedUser).isEqualTo(Optional.empty());
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