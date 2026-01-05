package com.homifybackend.exception;

public class AgentNotFoundException extends RuntimeException {

    public AgentNotFoundException(Long agentId) {
        super("Agent not found with ID: " + agentId);
    }

    public AgentNotFoundException(String message) {
        super(message);
    }
}