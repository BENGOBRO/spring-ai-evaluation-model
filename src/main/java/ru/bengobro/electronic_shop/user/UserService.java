package ru.bengobro.electronic_shop.user;

import org.springframework.stereotype.Service;
import ru.bengobro.electronic_shop.web.NotFoundException;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public User getById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }
}
