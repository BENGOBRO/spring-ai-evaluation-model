package ru.bengobro.electronic_shop.agent;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST-точка ручной проверки агента. */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agent;

    public AgentController(AgentService agent) {
        this.agent = agent;
    }

    @PostMapping("/chat")
    public AgentInvocation chat(@Valid @RequestBody AgentChatRequest request) {
        return agent.chat(request.query());
    }
}
