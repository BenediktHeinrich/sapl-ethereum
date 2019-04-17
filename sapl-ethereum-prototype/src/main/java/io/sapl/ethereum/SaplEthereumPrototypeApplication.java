package io.sapl.ethereum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SaplEthereumPrototypeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaplEthereumPrototypeApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void ethereumSetup() {
	    
	}

}
