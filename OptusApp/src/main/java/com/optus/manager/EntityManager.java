package com.optus.manager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 
 * @author Subbu
 *
 */
@Configuration
public class EntityManager {

	@Bean
	public FileDataManager fileManager(){
		return new FileDataManager();
	}
}
