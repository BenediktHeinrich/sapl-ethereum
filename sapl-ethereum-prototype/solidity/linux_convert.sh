#!/bin/bash
# A tool to convert Solidity Contracts to their Java from usable with Web3j.
# The Converter creates abi and bin files in ./abi-bin and saves the Java form
# directly in ../src/main/java/io/sapl/ethereum/contracts
# Needed input is the name of the contract (without .sol)
# Web3j CommandLineTools need to be installed (https://docs.web3j.io/command_line.html)


WEB3J='./web3j-4.2.0/bin/web3j'

solc $1.sol --bin --abi --optimize -o ./abi-bin --overwrite
$WEB3J solidity generate -b ./abi-bin/$1.bin -a ./abi-bin/$1.abi -o ../src/main/java -p io.sapl.ethereum.contracts
