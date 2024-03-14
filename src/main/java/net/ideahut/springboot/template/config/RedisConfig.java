package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.redis.RedisHelper;
import net.ideahut.springboot.redis.RedisProperties;
import net.ideahut.springboot.template.AppProperties;

@Configuration
class RedisConfig {
	
	@Autowired
	private AppProperties appProperties;
	
	@Bean
	protected RedisTemplate<String, byte[]> cacheRedis() throws Exception {
		RedisProperties properties = appProperties.getRedis();
		RedisConnectionFactory connectionFactory = RedisHelper.createRedisConnectionFactory(properties, true);
		return RedisHelper.createRedisTemplate(connectionFactory, false);
	}
	
}
