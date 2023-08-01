package flab.commercemarket.user.mapper;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.domain.user.mapper.UserMapper;
import flab.commercemarket.domain.user.vo.Authority;
import flab.commercemarket.domain.user.vo.User;
import flab.commercemarket.domain.user.vo.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.config.name=application-test")
@Transactional
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("유저 추가")
    void save() {
        //given
        User user = makeUserFixture(1);

        //when
        int insertedRowCnt = userMapper.save(user);

        //then
        assertThat(userMapper.findById(user.getId())).isPresent();
        User foundUser = userMapper.findById(user.getId()).get();
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("유저 권한 추가")
    void saveUserRole() {
        //given
        User user = makeJoinedUserFixture(1);
        //when
        UserRole userRole = new UserRole(user.getId(), Authority.BUYER.getAuthorityValue());
        int savedRowCnt = userMapper.saveUserRole(userRole);

        //then
        Optional<UserRole> roleById = userMapper.findRoleById(user.getId());
        assertThat(roleById).isPresent();
        UserRole presentUserRole = roleById.get();

        assertThat(Authority.valueOf(presentUserRole.getAuthority())).isEqualTo(Authority.BUYER);
    }

    @Test
    @DisplayName("유저 권한 찾기")
    void findRoleById() {
        //given
        User user = makeJoinedUserFixture(1);
        UserRole userRole = new UserRole(user.getId(), Authority.BUYER.getAuthorityValue());
        userMapper.saveUserRole(userRole);

        //when
        assertThat(userMapper.findRoleById(user.getId())).isPresent();
        UserRole foundUserRole = userMapper.findRoleById(user.getId()).get();

        //then
        assertThat(foundUserRole.getAuthority()).isEqualTo(Authority.BUYER.getAuthorityValue());
    }

    @Test
    @DisplayName("내부 로직 목적, id로 유저 찾기")
    void findById() {
        //given
        User user1 = makeUserFixture(1);
        userMapper.save(user1);

        User user2 = makeUserFixture(2);
        userMapper.save(user2);

        //when
        assertThat(userMapper.findByUsername(user1.getUsername())).isPresent();
        User foundUser1 = userMapper.findByUsername(user1.getUsername()).get();
        assertThat(userMapper.findByUsername(user2.getUsername())).isPresent();
        User foundUser2 = userMapper.findByUsername(user2.getUsername()).get();

        //then
        assertThat(foundUser1).isEqualTo(user1);
        assertThat(foundUser2).isEqualTo(user2);
    }

    @Test
    @DisplayName("모든 유저 찾기")
    void findAll() {
        //given
        User user1 = makeUserFixture(1);
        userMapper.save(user1);

        User user2 = makeUserFixture(2);
        userMapper.save(user2);

        User user3 = makeUserFixture(3);
        userMapper.save(user3);

        //when
        List<User> users = userMapper.findAll();

        //then
        assertThat(users.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("유저 업데이트")
    void update() {
        //given
        User user1 = makeUserFixture(1);
        int savedRowCnt = userMapper.save(user1);
        User userForUpdate = makeUserFixture(2);

        //when
        int updatedRowCnt = userMapper.update(user1.getId(), userForUpdate);
        assertThat(userMapper.findByUsername(userForUpdate.getUsername())).isPresent();
        User updatedUser = userMapper.findByUsername(userForUpdate.getUsername()).get();
        userForUpdate.setId(updatedUser.getId());

        //then
        assertThat(updatedUser).isEqualTo(userForUpdate);
    }

    @Test
    @DisplayName("유저 권한 업데이트")
    void updateUserRole() {
        //given
        User user = makeJoinedUserFixture(1);
        UserRole userRole = new UserRole(user.getId(), Authority.BUYER.getAuthorityValue());
        userMapper.saveUserRole(userRole);

        //when
        userMapper.updateUserRole(user.getId(), Authority.SELLER.getAuthorityValue());

        //then
        Optional<UserRole> roleById = userMapper.findRoleById(user.getId());
        assertThat(roleById).isPresent();
        UserRole foundUserRole = roleById.get();

        assertThat(foundUserRole.getAuthority()).isEqualTo(Authority.SELLER.getAuthorityValue());
    }

    @Test
    @DisplayName("유저 찾기 ByNameAndUsername")
    void findOne() {
        //given
        User user1 = makeUserFixture(1);
        User user2 = makeUserFixture(2);
        int savedRowCnt = userMapper.save(user1);
        int savedRowCnt2 = userMapper.save(user2);

        //when
        assertThat(userMapper.findByNameAndUsername(
                user1.getName(), user1.getUsername())).isPresent();
        User foundUser = userMapper.findByNameAndUsername(
                user1.getName(), user1.getUsername()).get();

        //then
        assertThat(foundUser).isEqualTo(user1);
    }

    @Test
    @DisplayName("유저 삭제")
    void delete() {
        //given
        User user1 = makeUserFixture(1);
        User user2 = makeUserFixture(2);
        int savedRowCnt = userMapper.save(user1);

        //when
        assertThat(userMapper.findByUsername(user1.getUsername())).isPresent();
        User savedUser = userMapper.findByUsername(user1.getUsername()).get();
        userMapper.delete(savedUser.getId());

        //then
        Optional<User> deletedUser = userMapper.findById(savedUser.getId());
        assertThat(deletedUser).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("유저 권한 삭제")
    void deleteUserRole() {
        //given
        User user = makeJoinedUserFixture(1);
        UserRole userRole = new UserRole(user.getId(), Authority.BUYER.getAuthorityValue());
        userMapper.saveUserRole(userRole);

        //when
        int deletedRowCnt = userMapper.deleteUserRole(user.getId());

        //then
        Optional<UserRole> deletedRole = userMapper.findRoleById(user.getId());
        assertThat(deletedRole).isEqualTo(Optional.empty());
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
        userMapper.save(user1);

        Optional<User> foundUser = userMapper.findByNameAndUsername(user1.getName(), user1.getUsername());
        return foundUser.orElseThrow(() -> new DataNotFoundException("테스트 유저가 생성되지 않아 조회에 실패"));
    }
}