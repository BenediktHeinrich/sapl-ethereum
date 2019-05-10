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
	private static final String USER1WALLET = 
			"UTC--2019-05-10T11-32-05.64000000Z--70b6613e37616045a80a97e08e930e1e4d800039.json";
	
	private final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(EthereumTestPIP.class);
	
	@Attribute(name="authEnv", docs="check if a user is authorized")
	public JsonNode authorizedWithEnvironment(JsonNode user, Map<String, JsonNode> variables) {
	logger.trace("Entered authorized now...");
	Web3j web3j = Web3j.build(new HttpService());
	try {
		
		logger.info("" + variables);
		
		String password = variables.get("environment").get("password").textValue();
		String wallet = variables.get("environment").get("wallet").textValue();
		
		Credentials credentials = WalletUtils.loadCredentials(password, wallet);

		UserAndContract ethUser = mapper.convertValue(user, UserAndContract.class);
		
		String contractAddress = ethUser.getEthContract();
		Authorization authContract = Authorization.load(contractAddress , web3j, credentials, new DefaultGasProvider());
		return mapper.convertValue(authContract.isAuthorized(ethUser.getEthAddress()).send(), JsonNode.class);
	} catch (Exception e) {
		logger.error("authorizedWithEnvironment didn't work as expected.");
		e.printStackTrace();
	}
	logger.debug("Returning null...");
	return JsonNodeFactory.instance.nullNode();
	}


}
