package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    List<User> getUsers();

    User getUserById(String userId);

    User loginUser(String name, String password);

    User loginUserGoogle(String token);

    User updateUser(String userId, User changeUser);

    User setRoleUser(String userId, String role);

    User deleteUser(String userId);

    void validateUser(User user);
}
