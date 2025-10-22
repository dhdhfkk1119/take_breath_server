package com.take.take_breath.chat;

import com.take.take_breath.chat.message.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private final List<ChatMessage> messages = new ArrayList<>();

    public void saveMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}
