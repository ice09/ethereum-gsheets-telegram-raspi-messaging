# ethereum-gsheets-telegram-raspi-messaging

## DIY: Build Stupid and Crazy Expensive Messaging for LED Switches

<img src="https://user-images.githubusercontent.com/2828581/161578594-9472a52c-42ad-41a1-934a-787f750cd214.png" width="60%"/>

## Setup

### Start on Raspberry Pi

_Note: Pi4j must be invoked as **sudo**_

```
sudo `which java` -jar raspi-messaging-0.0.1-SNAPSHOT.jar --spring.config.location=./application.properties --spring.profiles.active=prod
```

### Configure Messaging

You can choose between the Messaging types *GoogleSheet*,*TelegramBot*, or *Web3* by modifying the `application.properties`.

```
web3.module.enabled=${WEB3_MODULE_ENABLED:true}
gsheet.module.enabled=${GSHEET_MODULE_ENABLED:false}
tgram.module.enabled=${TELEGRAM_MODULE_ENABLED:false}
```

In the sample configuration above, *Web3* is enabled by default.  
However, all parameters have to be set by setting the environment variables.

## Web3 Messaging

For *Web3* Messaging, these parameters have to be set:

```
web3.ethereum.rpc.url=${ETHEREUM_NODE_IP_PORT:https://dai.poa.network/}
web3.switch.contract.address=${ETHEREUM_CONTRACT:0x192FaabCf853eA9da3AC92471989A1608E1f2676}
web3.mnemonic=${ETHEREUM_MNEMONIC:test test test test test test test test test test test junk}
```

* For the Ethereum RPC Url, you can look at http://pokt.network or use https://dai.poa.network/ for testing purposes.
* The Gnosis Chain contract address (if not set, contract is deployed)
* Mnemonic for account address, which is used to derive the private keys of the accounts

