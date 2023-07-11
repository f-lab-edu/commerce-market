package flab.commercemarket.user.repository;

import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemoryUserRepository implements UserRepository {
    private static final Map<Long, User> userStore = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public User save(User user) {
        user.setId(++sequence);
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    @Override
    public Optional<User> findByNameAndUsername(String name, String username) {
        return userStore.values().stream()
                .filter(user -> user.getName().equals(name) && user.getUsername().equals(username))
                .findAny();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userStore.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    @Override
    public User update(Long id, User userForUpdate) {
        Optional<User> findUser = findById(id);
        User extendUser;
        if (findUser.isPresent()) {
            extendUser = findUser.get();
        } else {
            throw new DataNotFoundException("존재하지 않는 id 입니다.");
        }

        extendUser.setName(userForUpdate.getName());
        extendUser.setAddress(userForUpdate.getAddress());
        extendUser.setEmail(userForUpdate.getEmail());
        extendUser.setPhoneNumber(userForUpdate.getPhoneNumber());
        extendUser.setUsername(userForUpdate.getUsername());
        extendUser.setPassword(userForUpdate.getPassword());

        return extendUser;
    }

    @Override
    public void delete(Long id) {
        userStore.remove(id);
    }

    public void clearStore() {
        userStore.clear();
    }
}
