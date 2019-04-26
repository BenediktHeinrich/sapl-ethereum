# Working with SAPL and Ethereum

This repository is about how to use an Ethereum contract inside a SAPL Policy. Therefore a Policy Information Point is used, which connects to the Ethereum Blockchain and provides information about the ethereum user.

## The SaplEthereumPrototype

The `SaplEthererumPrototype` uses the [Web3j API](https://web3j.readthedocs.io/en/latest/) to connect to the Ethereum Blockchain. It demonstrates some features of Ethereum, like creating a Wallet File (similar to a User Account), deploy a contract and interact with said contract to change the authorization state of users. To connect Ethereum with SAPL, a PIP is used, so that the information of this contract can be used directly inside a SAPL Policy.

### Getting started

Here you can find information on starting this Prototype.

1. Clone this Repository.
1. Download and install [Geth](https://geth.ethereum.org/downloads/) (This has been tested with version `1.8.27-stable`).
1. Navigate to the `ethereum-testnet` folder inside the project in a terminal or the PowerShell.
1. Execute the `startChain` script to initialize and start a private, local version of the Ethereum blockchain.
1. Start the `SaplEthererumPrototype` Application.