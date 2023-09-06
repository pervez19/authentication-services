package com.mycompany.authenticationservices;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "restfull web service",version = "v1",description = "spring doc"))
public class AuthenticationServicesApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServicesApplication.class, args);
	}

}
