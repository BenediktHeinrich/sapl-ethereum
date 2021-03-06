package io.sapl.ethereum.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.2.0.
 */
public class Authorization extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b031916331790556102c4806100326000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c8063a87430ba1461005c578063b6a5d7de14610096578063f0b37c04146100be578063f851a440146100e4578063fe9fbb8014610108575b600080fd5b6100826004803603602081101561007257600080fd5b50356001600160a01b031661012e565b604080519115158252519081900360200190f35b6100bc600480360360208110156100ac57600080fd5b50356001600160a01b0316610143565b005b6100bc600480360360208110156100d457600080fd5b50356001600160a01b03166101b6565b6100ec610223565b604080516001600160a01b039092168252519081900360200190f35b6100826004803603602081101561011e57600080fd5b50356001600160a01b0316610232565b60016020526000908152604090205460ff1681565b6000546001600160a01b0316331461018f57604051600160e51b62461bcd0281526004018080602001828103825260238152602001806102516023913960400191505060405180910390fd5b6001600160a01b03166000908152600160208190526040909120805460ff19169091179055565b6000546001600160a01b0316331461020257604051600160e51b62461bcd0281526004018080602001828103825260258152602001806102746025913960400191505060405180910390fd5b6001600160a01b03166000908152600160205260409020805460ff19169055565b6000546001600160a01b031681565b6001600160a01b031660009081526001602052604090205460ff169056fe4f6e6c79207468652061646d696e2063616e20617574686f72697a652075736572732e4f6e6c79207468652061646d696e2063616e20756e617574686f72697a652075736572732ea165627a7a723058200e9df01c53697785630ce61fe8d13a57932e3f9a2090709bc8ce1b88d4f4ff060029";

    public static final String FUNC_USERS = "users";

    public static final String FUNC_AUTHORIZE = "authorize";

    public static final String FUNC_UNAUTHORIZE = "unauthorize";

    public static final String FUNC_ADMIN = "admin";

    public static final String FUNC_ISAUTHORIZED = "isAuthorized";

    @Deprecated
    protected Authorization(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Authorization(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Authorization(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Authorization(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Boolean> users(String param0) {
        final Function function = new Function(FUNC_USERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> authorize(String user) {
        final Function function = new Function(
                FUNC_AUTHORIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(user)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> unauthorize(String user) {
        final Function function = new Function(
                FUNC_UNAUTHORIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(user)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> admin() {
        final Function function = new Function(FUNC_ADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> isAuthorized(String user) {
        final Function function = new Function(FUNC_ISAUTHORIZED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static Authorization load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Authorization(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Authorization load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Authorization(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Authorization load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Authorization(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Authorization load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Authorization(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Authorization> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Authorization.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Authorization> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Authorization.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Authorization> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Authorization.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Authorization> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Authorization.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
