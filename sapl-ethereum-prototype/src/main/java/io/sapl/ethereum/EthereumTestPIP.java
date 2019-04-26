package io.sapl.ethereum;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.ethereum.contracts.Authorization;

@Service
@PolicyInformationPoint(name="ethereum", description="provides functions for connecting with ethereum contracts")
public class EthereumTestPIP {
	
	private static final String KEYSTORE = "ethereum-testnet/ptn/keystore/";
	
	private final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(EthereumTestPIP.class);
	
	@Attribute(name="auth", docs="check if a user is authorized")
	public JsonNode authorized(JsonNode user, Map<String, JsonNode> variables) {
	logger.trace("Entered authorized now...");
	Web3j web3j = Web3j.build(new HttpService());
	try {
		
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
    	String devUser = accounts.get(0);
		
		String devUserWallet = EthServices.getUserWallet(devUser, KEYSTORE);
		
		Credentials credentials = WalletUtils.loadCredentials("", devUserWallet);
		EthUser ethUser = mapper.convertValue(user, EthUser.class);
		
		String contractAddress = ethUser.getEthContract();
		Authorization authContract = Authorization.load(contractAddress , web3j, credentials, new DefaultGasProvider());
		return mapper.convertValue(authContract.isAuthorized(ethUser.getEthAddress()).send(), JsonNode.class);
	} catch (Exception e) {
		logger.error("The EthereumTestPip didn't work as expected.");
		e.printStackTrace();
	}
	logger.debug("Returning null...");
	return JsonNodeFactory.instance.nullNode();
	}


}
