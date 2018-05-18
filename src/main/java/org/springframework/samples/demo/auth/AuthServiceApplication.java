package org.springframework.samples.demo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;

/**
 * @author wdongyu
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AuthServiceApplication {

    @Bean 
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }

    public static void main(String[] args) {
	new SpringApplicationBuilder(AuthServiceApplication.class).web(true).run(args);
        //SpringApplication.run(DbServiceApplication.class, args);
    }

}
