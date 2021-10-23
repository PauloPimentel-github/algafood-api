package com.phpimentel.algafoodapi;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.phpimentel.algafoodapi.infrastructure.repository.CustomJpaRepositoryImpl;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
@EnableCaching
public class AlgafoodApiApplication {

    public static void main(String[] args) {
    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(AlgafoodApiApplication.class, args);
    }

}
