package io.sapl.ethereum;


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
	
	private static final String USER1_WALLET = "/home/bene/ethereum-testnet/ptn/keystore/UTC--2019-04-17T21-39-40.596498485Z--2678c7e529d61f14f7711053be92d0a923cda8d2";
	private static final String USER1 = "0x2678c7e529d61f14f7711053be92d0a923cda8d2";
	private static final String USER2 = "0x91b6eac43acf5fc115fb30bf8ecc348d1c8d474b";
	private static final String ACCESS = "access";
	private static final String ETHEREUM = "ethereum";

	public static void main(String[] args) {
		SpringApplication.run(SaplEthereumPrototypeApplication.class, args);
	}
	
	
	@EventListener(ApplicationReadyEvent.class)
	public void ethereumSetup() {
	    Web3j web3 = Web3j.build(new HttpService());
	    try {
			Credentials credentials = WalletUtils.loadCredentials("", USER1_WALLET);
			Authorization authContract = Authorization.deploy(web3, credentials, new DefaultGasProvider()).send();
			
			String contractAddress = authContract.getContractAddress();
			
			System.out.println("Authorization contract deployed under address: \n" + contractAddress);
			
			System.out.println("User 1 is authorized: " + 
						authContract.isAuthorized(USER1).send());
			System.out.println("User 2 is authorized: " + 
						authContract.isAuthorized(USER2).send());
			
			authContract.authorize(USER2).send();
			
			System.out.println("User 1 is authorized: " + 
					authContract.isAuthorized(USER1).send());
			System.out.println("User 2 is authorized: " + 
					authContract.isAuthorized(USER2).send());
			
			Builder builder = EmbeddedPolicyDecisionPoint.builder();
			builder = builder.withPolicyInformationPoint(new EthereumTestPIP());
			EmbeddedPolicyDecisionPoint pdp = builder.build();
			
			EthUser user1 = new EthUser(USER1, contractAddress);
			EthUser user2 = new EthUser(USER2, contractAddress);
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode user1json = mapper.convertValue(user1, JsonNode.class);
			JsonNode user2json = mapper.convertValue(user2, JsonNode.class);
			System.out.println(user1json);
			JsonNode accessJson = mapper.convertValue(ACCESS, JsonNode.class);
			JsonNode ethereumJson = mapper.convertValue(ETHEREUM, JsonNode.class);
			Request user1Request = new Request(user1json, accessJson, ethereumJson, ethereumJson);
			Request user2Request = new Request(user2json, accessJson, ethereumJson, ethereumJson);
			
			JsonNode aJson = mapper.convertValue("a", JsonNode.class);
			JsonNode bJson = mapper.convertValue("b", JsonNode.class);
			Request testRequest = new Request(user1json, aJson, bJson, bJson);
			
			Flux<Response> user1access = pdp.decide(user1Request);
			Flux<Response> user2access = pdp.decide(user2Request);
			Flux<Response> simpleTest = pdp.decide(testRequest);
			System.out.println("User1 Response: " + user1access.blockFirst());
			System.out.println("User2 Response: " + user2access.blockFirst());
			System.out.println(simpleTest.blockFirst());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}

}
