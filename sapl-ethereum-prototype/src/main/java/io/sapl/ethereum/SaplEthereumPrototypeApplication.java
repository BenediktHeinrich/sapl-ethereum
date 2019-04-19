package io.sapl.ethereum;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import io.sapl.ethereum.contracts.Authorization;

@SpringBootApplication
public class SaplEthereumPrototypeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaplEthereumPrototypeApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void ethereumSetup() {
	    Web3j web3 = Web3j.build(new HttpService());
	    try {
			Credentials credentials = WalletUtils.loadCredentials("", "/home/bene/ethereum-testnet/ptn/keystore/UTC--2019-04-17T21-39-40.596498485Z--2678c7e529d61f14f7711053be92d0a923cda8d2");

			Authorization authContract = Authorization.deploy(web3, credentials, new DefaultGasProvider()).send();
			System.out.println("User 1 is authorized: " + 
						authContract.isAuthorized("0x2678c7e529d61f14f7711053be92d0a923cda8d2").send());
			System.out.println("User 2 is authorized: " + 
						authContract.isAuthorized("0x91b6eac43acf5fc115fb30bf8ecc348d1c8d474b").send());
			
			authContract.authorize("0x91b6eac43acf5fc115fb30bf8ecc348d1c8d474b").send();
			
			System.out.println("User 1 is authorized: " + 
					authContract.isAuthorized("0x2678c7e529d61f14f7711053be92d0a923cda8d2").send());
			System.out.println("User 2 is authorized: " + 
					authContract.isAuthorized("0x91b6eac43acf5fc115fb30bf8ecc348d1c8d474b").send());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}

}
