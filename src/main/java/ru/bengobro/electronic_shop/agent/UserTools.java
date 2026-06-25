package ru.bengobro.electronic_shop.agent;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import ru.bengobro.electronic_shop.user.UserResponse;
import ru.bengobro.electronic_shop.user.UserService;

/** Инструменты агента над пользователями. */
@Component
public class UserTools {

    private final UserService users;
    private final AgentInvocationRecorder recorder;

    public UserTools(UserService users, AgentInvocationRecorder recorder) {
        this.users = users;
        this.recorder = recorder;
    }

    @Tool(description = "Получить профиль пользователя по идентификатору")
    public UserResponse getUserById(
            @ToolParam(description = "идентификатор пользователя") Long userId) {
        recorder.record("getUserById", ToolArgs.of("userId", userId));
        return UserResponse.from(users.getById(userId));
    }
}
