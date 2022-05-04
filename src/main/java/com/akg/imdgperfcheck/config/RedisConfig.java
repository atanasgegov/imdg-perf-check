package com.akg.imdgperfcheck.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.redisearch.client.Client;
import lombok.Getter;
import lombok.Setter;

@Configuration("redisConfig")
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
public class RedisConfig extends AbstractConfig {
	
	@Bean
	public Client getClient() {
		return new Client("idx:wine", "localhost", 6379);
	}
}