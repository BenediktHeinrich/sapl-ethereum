package io.sapl.ethereum;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.Response;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.ethereum.contracts.Authorization;

@SpringBootApplication
public class SaplEthereumPrototypeApplication {
	
	public final String USER1_WALLET = "/home/bene/ethereum-testnet/ptn/keystore/UTC--2019-04-17T21-39-40.596498485Z--2678c7e529d61f14f7711053be92d0a923cda8d2";
	public final String USER1 = "0x2678c7e529d61f14f7711053be92d0a923cda8d2";
	public final String USER2 = "0x91b6eac43acf5fc115fb30bf8ecc348d1c8d474b";

	public static void main(String[] args) {
		SpringApplication.run(SaplEthereumPrototypeApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void ethereumSetup() {
	    Web3j web3 = Web3j.build(new HttpService());
	    try {
			Credentials credentials = WalletUtils.loadCredentials("", USER1_WALLET);
			Authorization authContract = Authorization.deploy(web3, credentials, new DefaultGasProvider()).send();
			
			System.out.println("Authorization contract deployed under address: \n" + authContract.getContractAddress());
			
			System.out.println("User 1 is authorized: " + 
						authContract.isAuthorized(USER1).send());
			System.out.println("User 2 is authorized: " + 
						authContract.isAuthorized(USER2).send());
			
			authContract.authorize(USER2).send();
			
			System.out.println("User 1 is authorized: " + 
					authContract.isAuthorized(USER1).send());
			System.out.println("User 2 is authorized: " + 
					authContract.isAuthorized(USER2).send());
			
			PolicyDecisionPoint pdp = new EmbeddedPolicyDecisionPoint("file:/home/bene/repos-linux/sapl-ethereum/sapl-ethereum-prototype/src/main/resources/policies/");
			Response user1access = pdp.decide(USER1, "access", "ethereum");
			Response user2access = pdp.decide(USER2, "access", "ethereum");
			System.out.println("User1 Response: " + user1access);
			System.out.println("User2 Response: " + user2access);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}

}
