package com.mycompany.authenticationservices;

import com.mycompany.authenticationservices.serviceImpl.GmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthenticationServicesApplicationTests {

	@Autowired
	private GmailServiceImpl service;


	@Test
	void contextLoads() throws Exception {
//		service.sendMail("A new message", """
//                Dear reader,
//
//                Hello world.
//
//                Best regards,
//                myself
//                ""","pervezdiu16@gmail.com");
		service.receivedMail();
	}

}
