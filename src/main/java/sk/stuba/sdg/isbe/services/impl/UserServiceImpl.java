package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.User;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.UserRepository;
import sk.stuba.sdg.isbe.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final HttpTransport transport = new NetHttpTransport();

    @Autowired
    private UserRepository userRepository;

    @Value("${sdg.google-client-id}")
    private String googleClientId;

    @Override
    public User createUser(User user) {
        validateUser(user);

        user.setCreatedAt(Instant.now().toEpochMilli());
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidEntityException("User id is not set!");
        }
        return userRepository.getUserByUid(userId);
    }

    @Override
    public User loginUser(String name, String password) {
        User user = userRepository.getUserByNameAndPassword(name, password);
        if (user.getName() == null || user.getName().isEmpty() || user.getPassword() == null
                || user.getPassword().isEmpty()) {
            throw new EntityExistsException("User name or password wrong!");
        }

        return user;
    }

@Override
public User loginUserGoogle(String token) {
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, new GsonFactory())
            .setAudience(Collections.singletonList(googleClientId))
            .build();
    try {
        GoogleIdToken idToken = verifier.verify(token);

        if (idToken != null) {
            Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            User user = userRepository.getUserByMail(email);

            if (user == null) {
                user = new User();
                user.setMail(email);
                user.setName((String) payload.get("name"));
                user.setCreatedAt(Instant.now().toEpochMilli());
                userRepository.save(user);
                return user;
            } else {
                return user;
            }
        } else {
            System.out.println("Invalid ID token.");
            return null;
        }
    } catch (GeneralSecurityException e) {
        System.out.println("Security exception while verifying Google ID token: " + e.getMessage());
        return null;
    } catch (IOException e) {
        System.out.println("IO exception while verifying Google ID token: " + e.getMessage());
        return null;
    }
}


    @Override
    public User updateUser(String userId, User changeUser) {
        User user = getUserById(userId);

        if (userId == null || userId.isEmpty()) {
            throw new InvalidEntityException("userId with changes is null!");
        }

        if (changeUser.getName() != null) {
            user.setName(changeUser.getName());
        }
        if (changeUser.getMail() != null) {
            user.setMail(changeUser.getMail());
        }
        if (changeUser.getPassword() != null) {
            user.setPassword(changeUser.getPassword());
        }
        if (changeUser.getAuthorities() != null) {
            user.setAuthorities(changeUser.getAuthorities());
        }

        return userRepository.save(user);
    }

    @Override
    public User setRoleUser(String userId, String role) {
        User user = getUserById(userId);

        if (userId == null || userId.isEmpty()) {
            throw new InvalidEntityException("userId with changes is null!");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    @Override
    public User deleteUser(String userId) {
        User user = getUserById(userId);
        user.setDeactivated(true);
        return userRepository.save(user);
    }

    @Override
    public void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidEntityException("User name is not set!");
        }
        if (user.getMail() == null || user.getMail().isEmpty()) {
            throw new InvalidEntityException("User mail is not set!");
        }
        if (userRepository.getUserByMail(user.getMail()) != null) {
            throw new InvalidEntityException("User with mail exist!");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new InvalidEntityException("User password is not set!");
        }
        // if (user.getPermissions() == null) {
        // throw new InvalidEntityException("User permissions is not set!");
        // }
    }
}
