package com.akg.imdgperfcheck.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.akg.imdgperfcheck.config.pojo.UseCases;
import com.akg.imdgperfcheck.config.pojo.UseCases.Type;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "commons")
@Data
public class CommonConfig {

	private String inputDataFile;
	private int batchSize;
	private long frequencyOutputInMs;
	private Type activeUseCase = UseCases.Type.ONE;
	private UseCases useCases;
}
