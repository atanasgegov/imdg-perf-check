package com.akg.imdgperfcheck.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akg.imdgperfcheck.dto.WineDTO;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import lombok.Getter;
import lombok.Setter;

@Configuration("hazelcastConfig")
@ConfigurationProperties(prefix = "hazelcast")
@Getter
@Setter
public class HazelcastConfig extends AbstractConfig {

	private String clusterName;
	
	@Bean
	public HazelcastInstance getHazelcastInstance() {

		if( clusterName != null ) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.setClusterName(clusterName);
			clientConfig.getNetworkConfig().addAddress(host+":"+port);
			
			return HazelcastClient.newHazelcastClient(clientConfig);
		}
		
		return null;
	}
}
