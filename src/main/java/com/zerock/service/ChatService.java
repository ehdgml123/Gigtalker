package com.zerock.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;


    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    //기본적인 메세지 요청하는것
    public String chat(String message){
        return chatClient.prompt()
                .user(message)  // 사용자 메시지
                .call()  // api 호출 하는것
                .content();    // gpt 응답
    }

    // 메시지 와 함께 요청
    public String chatWithSystemMessage(String systemMessage, String userMessage){

        return chatClient.prompt()
                .system(systemMessage)  // 시스템 역활 정의
                .user(userMessage)  // 사용자 메시지
                .call()
                .content();
    }

    public String chatJson(String message){
        return chatClient.prompt()
                .user(message)
                .call()
                .chatResponse()
                .toString();  // json 으로 응답 변환
    }
}
