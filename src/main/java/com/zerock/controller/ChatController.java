package com.zerock.controller;

import com.zerock.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    //기본 메시지 요청
    @GetMapping
    public String chat(@RequestParam String message){
        return chatService.chat(message);
    }

    // 메시지와 사용자 메시지 요청
    @GetMapping("/")
    public String chatWithSystemMessage(@RequestParam String systemMessage, @RequestParam String userMessage) {
        return chatService.chatWithSystemMessage(systemMessage, userMessage);
    }

    // JSON 형식으로 응답 반환
    @GetMapping("/json")
    public String chatJson(@RequestParam String message) {
        return chatService.chatJson(message);
    }

}

