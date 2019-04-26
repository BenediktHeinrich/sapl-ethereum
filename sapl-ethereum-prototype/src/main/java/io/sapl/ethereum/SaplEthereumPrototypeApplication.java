package io.sapl.ethereum;


import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder;
import reactor.core.publisher.Flux;
import io.sapl.ethereum.contracts.Authorization;

@EntityScan
@SpringBootApplication
public class SaplEthereumPrototypeApplication {
	
	private static final String KEYSTORE = "ethereum-testnet/ptn/keystore/";
	private static final String ACCESS = "access";
	private static final String ETHEREUM = "ethereum";
	
	private static final Logger logger = LoggerFactory.getLogger(SaplEthereumPrototypeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SaplEthereumPrototypeApplication.class, args);
	}
	
	
	@EventListener(ApplicationReadyEvent.class)
	public void ethereumSetup() {
		Web3j web3j = Web3j.build(new HttpService());

	    try {
	    	
	    	// We create our second user through WalletUtils.
	    	// First user is the Dev User, which is automatically created through 
	    	// the --dev option in geth.
	    	String user2wallet = KEYSTORE + WalletUtils.generateNewWalletFile("", new File(KEYSTORE));

	    	List<String> accounts = web3j.ethAccounts().send().getAccounts();
	    	// Here we have to wait for the new Wallet File to be recognized by the blockchain
	    	while(accounts.size() < 2) {
	    		accounts = web3j.ethAccounts().send().getAccounts();
	    	}
	    	String user1 = accounts.get(0);
	    	String user2 = accounts.get(1);
	    	
	    	// Now we have to extract the name of the Wallet File for the Dev User.
	    	String user1wallet = EthServices.getUserWallet(user1, KEYSTORE);
	    	
	    	logger.info("WalletFile for User 1: " + user1wallet); 
	    	logger.info("WalletFile for User 2: " + user2wallet);
			
	    	// Now we use the Dev User Account, which already comes with ether, to deploy a new contract.
	    	// The original contract can be reviewed in the "solidity" folder.
	    	Credentials credentials = WalletUtils.loadCredentials("", user1wallet);
			Authorization authContract = Authorization.deploy(web3j, credentials, new DefaultGasProvider()).send();
			String contractAddress = authContract.getContractAddress();
			
			logger.info("Authorization contract deployed under address: " + contractAddress);
			
			
			// In the following section User 2 becomes authorized in the contract.
			logger.info("User 1 is authorized: " + 
						authContract.isAuthorized(user1).send());
			logger.info("User 2 is authorized: " + 
						authContract.isAuthorized(user2).send());
			
			logger.info("Authorizing User 2...");
			authContract.authorize(user2).send();
			
			logger.info("User 1 is authorized: " + 
					authContract.isAuthorized(user1).send());
			logger.info("User 2 is authorized: " + 
					authContract.isAuthorized(user2).send());
			
			
			// Now we use a PIP to request information from the Ethereum contract.
			Builder builder = EmbeddedPolicyDecisionPoint.builder();
			builder = builder.withPolicyInformationPoint(new EthereumTestPIP());
			EmbeddedPolicyDecisionPoint pdp = builder.build();
			
			EthUser ethUser1 = new EthUser(user1, contractAddress);
			EthUser ethUser2 = new EthUser(user2, contractAddress);
			
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode user1json = mapper.convertValue(ethUser1, JsonNode.class);
			JsonNode user2json = mapper.convertValue(ethUser2, JsonNode.class);
			JsonNode accessJson = mapper.convertValue(ACCESS, JsonNode.class);
			JsonNode ethereumJson = mapper.convertValue(ETHEREUM, JsonNode.class);
			
			Request user1Request = new Request(user1json, accessJson, ethereumJson, ethereumJson);
			Request user2Request = new Request(user2json, accessJson, ethereumJson, ethereumJson);
			
			Flux<Response> user1access = pdp.decide(user1Request);
			Flux<Response> user2access = pdp.decide(user2Request);
			
			logger.info("User1 Response: " + user1access.blockFirst());
			logger.info("User2 Response: " + user2access.blockFirst());
			
			logger.info("Authorizing now User 1 and unauthorizing User 2...");
			authContract.authorize(user1).send();
			authContract.unauthorize(user2).send();
			
			user1access = pdp.decide(user1Request);
			user2access = pdp.decide(user2Request);
			
			logger.info("User1 Response: " + user1access.blockFirst());
			logger.info("User2 Response: " + user2access.blockFirst());
			
			logger.info("Application has finished.");

	    } catch (Exception e) {
			logger.error("Error in Main Application.");
			e.printStackTrace();
		}
 
	}

}
