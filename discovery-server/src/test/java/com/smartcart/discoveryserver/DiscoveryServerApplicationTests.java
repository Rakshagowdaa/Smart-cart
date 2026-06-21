package com.smartcart.discoveryserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiscoveryServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodTest() {
		try (org.mockito.MockedStatic<org.springframework.boot.SpringApplication> mocked = org.mockito.Mockito.mockStatic(org.springframework.boot.SpringApplication.class)) {
			mocked.when(() -> org.springframework.boot.SpringApplication.run(DiscoveryServerApplication.class, new String[]{}))
					.thenReturn(null);
			DiscoveryServerApplication.main(new String[]{});
			mocked.verify(() -> org.springframework.boot.SpringApplication.run(DiscoveryServerApplication.class, new String[]{}));
		}
	}
}
