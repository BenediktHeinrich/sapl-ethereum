package io.sapl.ethereum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.ethereum.contracts.Authorization;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder;
import reactor.core.publisher.Flux;

@EntityScan
@SpringBootApplication
public class SaplEthereumPrototypeApplication {

	private static final String KEYSTORE = "ethereum-testnet/ptn/keystore/";

	private static final String USER1WALLET = "UTC--2019-05-10T11-32-05.64000000Z--70b6613e37616045a80a97e08e930e1e4d800039.json";
	private static final String USER2WALLET = "UTC--2019-05-10T11-32-55.438000000Z--3f2cbea2185089ea5bbabbcd7616b215b724885c.json";
	private static final String USER3WALLET = "UTC--2019-05-10T11-33-01.363000000Z--2978263a3ecacb01c75e51e3f74b37016ee3904c.json";
	private static final String USER4WALLET = "UTC--2019-05-10T11-33-10.665000000Z--23a28c4cbad79cf61c8ad2e47d5134b06ef0bb73.json";

	private static final String PASSWORD = "";

	private static final String ACCESS = "access";
	private static final String ETHEREUM = "ethereum";
	private static final String ETH_POLICY = "ethPolicy";

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final Logger logger = LoggerFactory.getLogger(SaplEthereumPrototypeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SaplEthereumPrototypeApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void ethereumSetup() {
		Web3j web3j = Web3j.build(new HttpService());

		logger.info("-------------------------------------------SANDBOX--------------------------------------------");
		JsonNodeFactory factory = new JsonNodeFactory(true);
		JsonNode node = factory.numberNode(4567);
		logger.info("" + node);
		logger.info("" + node.bigIntegerValue());
		logger.info("" + DefaultBlockParameter.valueOf(node.bigIntegerValue()));

		logger.info("-----------------------------------------SANDBOX ENDE-----------------------------------------");
		try {

			// In this first section we load the accounts from the blockchain
			List<String> accounts = web3j.ethAccounts().send().getAccounts();
			String user1 = accounts.get(0);
			String user2 = accounts.get(1);
			String user3 = accounts.get(2);
			String user4 = accounts.get(3);

			logger.info("List of users: \nUser1: " + user1 + "\nUser2: " + user2 + "\nUser3: " + user3 + "\nUser4: "
					+ user4);

			// We make some transactions
			Credentials credentials = WalletUtils.loadCredentials("", KEYSTORE + USER1WALLET);
			TransactionReceipt transactionReceiptUser2 = Transfer
					.sendFunds(web3j, credentials, user2, BigDecimal.valueOf(2.0), Convert.Unit.ETHER).send();
			TransactionReceipt transactionReceiptUser3 = Transfer
					.sendFunds(web3j, credentials, user3, BigDecimal.valueOf(3.3), Convert.Unit.ETHER).send();
			TransactionReceipt transactionReceiptUser4 = Transfer
					.sendFunds(web3j, credentials, user4, BigDecimal.valueOf(4.444), Convert.Unit.ETHER).send();
			EthTransaction transaction = web3j.ethGetTransactionByHash(transactionReceiptUser2.getTransactionHash())
					.send();
			Optional<Transaction> optTrans = transaction.getTransaction();
			Transaction trans = optTrans.get();
			logger.info("Transaction Receipts: \nUser2: " + transactionReceiptUser2 + "\nUser3: "
					+ transactionReceiptUser3 + "\nUser4: " + transactionReceiptUser4);
			logger.info("Transaction: " + trans);
			logger.info("TRANSACTION VALUE: " + trans.getValue());

			// Now we use the first User Account, which already comes with ether, to deploy
			// a new contract.
			// The original contract can be reviewed in the "solidity" folder.
			Authorization authContract = Authorization.deploy(web3j, credentials, new DefaultGasProvider()).send();
			String contractAddress = authContract.getContractAddress();
			EthGetCode ethCode = web3j.ethGetCode(contractAddress, DefaultBlockParameter.valueOf("latest")).send();
			String code = ethCode.getCode();
			logger.info(code);

			logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			logger.info("Authorization contract deployed under address: " + contractAddress);
			logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

			// In the following section User 2 becomes authorized in the contract.

			logger.info("-------------ETHEREUM CONTRACT USAGE------------------------------------------");
			logger.info("User 1 is authorized: " + authContract.isAuthorized(user1).send());
			logger.info("User 2 is authorized: " + authContract.isAuthorized(user2).send());

			logger.info("Authorizing User 2...");
			authContract.authorize(user2).send();

			logger.info("User 1 is authorized: " + authContract.isAuthorized(user1).send());
			logger.info("User 2 is authorized: " + authContract.isAuthorized(user2).send());
			logger.info("----------------------------------------------------------------------------");

			// ETH_CALL TEST
			Function function = new Function("isAuthorized", Arrays.asList(new Address(user2)), // Solidity Types in
																								// smart contract
																								// functions
					Arrays.asList(new TypeReference<Bool>() {
					}));

			String encodedFunction = FunctionEncoder.encode(function);
			org.web3j.protocol.core.methods.response.EthCall response = web3j
					.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(null,
							contractAddress, encodedFunction), DefaultBlockParameterName.LATEST)
					.send();

			List<Type> someTypes = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

			logger.info("LIST TYPES: " + someTypes.get(0).getValue());

			org.web3j.protocol.core.methods.request.Transaction ethCallTransaction = org.web3j.protocol.core.methods.request.Transaction
					.createFunctionCallTransaction(null, null, null, null, contractAddress, null, null);
			EthCall ethCall = web3j.ethCall(ethCallTransaction, DefaultBlockParameter.valueOf("latest")).send();
			logger.info("ETH_CALL: " + ethCall);
			logger.info("RESULT: " + ethCall.getResult());

			// Now we use a PIP to request information from the Ethereum contract.
			Builder builder = EmbeddedPolicyDecisionPoint.builder();
			builder = builder.withResourcePDPConfigurationProvider();
			builder = builder.withPolicyInformationPoint(new EthereumTestPIP());
			EmbeddedPolicyDecisionPoint pdp = builder.build();

			UserAndContract ethUser1 = new UserAndContract(user1, contractAddress);
			UserAndContract ethUser2 = new UserAndContract(user2, contractAddress);

			JsonNode user1json = mapper.convertValue(ethUser1, JsonNode.class);
			JsonNode user2json = mapper.convertValue(ethUser2, JsonNode.class);
			JsonNode accessJson = mapper.convertValue(ACCESS, JsonNode.class);
			JsonNode ethereumJson = mapper.convertValue(ETHEREUM, JsonNode.class);
			JsonNode ethPolicyJson = mapper.convertValue(ETH_POLICY, JsonNode.class);

			Request user1Request = new Request(user1json, accessJson, ethereumJson, null);
			Request user2Request = new Request(user2json, accessJson, ethereumJson, null);
			Request user1EthPolicyRequest = new Request(user1json, accessJson, ethPolicyJson, null);
			Request user2EthPolicyRequest = new Request(user2json, accessJson, ethPolicyJson, null);

			Flux<Response> user1access = pdp.decide(user1Request);
			Flux<Response> user2access = pdp.decide(user2Request);

			Flux<Response> user1PolAccess = pdp.decide(user1EthPolicyRequest);
			Flux<Response> user2PolAccess = pdp.decide(user2EthPolicyRequest);

			logger.info("----------------------------------------------------------------------------");
			logger.info("--------USING ETHEREUM WITH CREDENTIALS SAVED IN PDP.JSON-------------------");
			logger.info("User1 Environment Response: " + user1access.blockFirst());
			logger.info("User2 Environment Response: " + user2access.blockFirst());

			logger.info("----------------------------------------------------------------------------");
			logger.info("--------USING ETHEREUM WITH CREDENTIALS FROM POLICY-------------------------");
			logger.info("User1 EthPolicy Response: " + user1PolAccess.blockFirst());
			logger.info("User2 EthPolicy Response: " + user2PolAccess.blockFirst());

			logger.info("----------------------------------------------------------------------------");
			logger.info("Authorizing now User 1 and unauthorizing User 2...");
			authContract.authorize(user1).send();
			authContract.unauthorize(user2).send();

			user1access = pdp.decide(user1Request);
			user2access = pdp.decide(user2Request);

			user1PolAccess = pdp.decide(user1EthPolicyRequest);
			user2PolAccess = pdp.decide(user2EthPolicyRequest);

			logger.info("----------------------------------------------------------------------------");
			logger.info("--------USING ETHEREUM WITH CREDENTIALS SAVED IN PDP.JSON-------------------");
			logger.info("User1 Environment Response: " + user1access.blockFirst());
			logger.info("User2 Environment Response: " + user2access.blockFirst());

			logger.info("----------------------------------------------------------------------------");
			logger.info("--------USING ETHEREUM WITH CREDENTIALS FROM POLICY-------------------------");
			logger.info("User1 EthPolicy Response: " + user1PolAccess.blockFirst());
			logger.info("User2 EthPolicy Response: " + user2PolAccess.blockFirst());

			logger.info("Application has terminated.");

		} catch (Exception e) {
			logger.error("Error in Main Application.");
			e.printStackTrace();
		}

	}

}
