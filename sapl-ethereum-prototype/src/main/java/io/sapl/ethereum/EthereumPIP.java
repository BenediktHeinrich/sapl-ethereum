package io.sapl.ethereum;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.ethereum.contracts.Authorization;

@PolicyInformationPoint(name="ethereum", description="checks if  user is authorized in ethereum contract")
public class EthereumPIP {
	
	@Attribute
	public boolean authorized(String user) {
	System.out.println("Entered authorized...");
	Web3j web3j = Web3j.build(new HttpService());
	try {
		Credentials credentials = WalletUtils.loadCredentials("", "/home/bene/ethereum-testnet/ptn/keystore/UTC--2019-04-17T21-39-40.596498485Z--2678c7e529d61f14f7711053be92d0a923cda8d2");
		
		String contractAddress = javax.swing.JOptionPane.showInputDialog( "Please enter address of Authorization contract.");
		Authorization authContract = Authorization.load(contractAddress , web3j, credentials, new DefaultGasProvider());
		return authContract.isAuthorized(user).send();
	} catch (Exception e) {
		
	}
	return false;
	}
	

}
