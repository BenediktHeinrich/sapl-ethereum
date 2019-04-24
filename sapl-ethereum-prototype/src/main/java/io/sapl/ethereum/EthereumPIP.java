package io.sapl.ethereum;

import java.util.Map;

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
public class EthereumPIP {
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Attribute(name="autho", docs="check if a user is authorized")
	public JsonNode authorized(JsonNode user, Map<String, JsonNode> variables) {
	System.out.println("Entered authorized...");
	Web3j web3j = Web3j.build(new HttpService());
	try {
		Credentials credentials = WalletUtils.loadCredentials("", "/home/bene/ethereum-testnet/ptn/keystore/UTC--2019-04-17T21-39-40.596498485Z--2678c7e529d61f14f7711053be92d0a923cda8d2");
		EthUser ethUser = mapper.convertValue(user, EthUser.class);
		
		String contractAddress = ethUser.ethContract;
		Authorization authContract = Authorization.load(contractAddress , web3j, credentials, new DefaultGasProvider());
		return mapper.convertValue(authContract.isAuthorized(ethUser.ethAddress).send(), JsonNode.class);
	} catch (Exception e) {
		
	}
	return JsonNodeFactory.instance.nullNode();
	}
	
	@Attribute(name="address")
	public JsonNode returnEthAddress (JsonNode user, Map<String, JsonNode> variables) {
		return mapper.convertValue(mapper.convertValue(user, EthUser.class).ethAddress, JsonNode.class);
	}

}
