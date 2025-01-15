package com.zerock.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    //ChatClient 객체를 Spring 컨텍스트에 Bean으로 등록하기 위한 구성

    @Bean
   public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {

       return chatClientBuilder.defaultAdvisors(
               //메모리 어드바이저 설정
               new MessageChatMemoryAdvisor(new InMemoryChatMemory())
       ).build();
   }
}

/*
* ChatClient.Builder 를 받아와 빌더 객체를 생성
*
* defaultAdvisors 메서드를 호출해 MessageChatMemoryAdvisor를 설정 한다
*  여기서 MessageChatMemoryAdvisor는 대화 데이터의 메모리 관리와 관련된 역할
*  InMemoryChatMemory를 통해 대화 데이터를 메모리에 저장하도록 지정
 */
