package com.zerock.config;

import com.zerock.Entity.Users;
import com.zerock.dto.UserFormDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // UserFormDto -> Users 매핑 설정
        modelMapper.typeMap(UserFormDto.class, Users.class).addMappings(mapper -> {
            mapper.map(UserFormDto::getProviderId, Users::setProviderId);
            mapper.skip(Users::setId); // id 필드 매핑 제외
        });

        return modelMapper;
    }
}
