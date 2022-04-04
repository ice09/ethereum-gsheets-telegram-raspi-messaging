package dev.iot.web3reader;

import dev.wickedenterprise.Switch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@ConditionalOnProperty(
        value="web3.module.enabled",
        havingValue = "true")
public class MonitorConfig {

    private final String ethereumRpcUrl;
    private final String switchAddress;
    private final String mnemonic;

    public MonitorConfig(@Value("${web3.ethereum.rpc.url}") String ethereumRpcUrl, @Value("${web3.switch.contract.address}") String switchAddress, @Value("${web3.mnemonic}") String mnemonic) {
        this.ethereumRpcUrl = ethereumRpcUrl;
        this.switchAddress = switchAddress;
        this.mnemonic = mnemonic;
    }

    @Bean
    public CredentialHolder createCredentials() {
        return new CredentialHolder(createMasterKeyPair());
    }

    public Bip32ECKeyPair createMasterKeyPair() {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");
        return Bip32ECKeyPair.generateKeyPair(seed);
    }

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(ethereumRpcUrl, createOkHttpClient()));
    }

    @Bean
    public Switch createSwitchProxy() throws Exception {
        if (StringUtils.hasText(switchAddress)) {
            return Switch.load(switchAddress, web3j(), createCredentials().deriveChildKeyPair(0), new DefaultGasProvider());
        } else {
            Switch switchContract = Switch.deploy(web3j(), createCredentials().deriveChildKeyPair(0), new DefaultGasProvider()).send();
            log.info("Deployed Switch contract at address " + switchContract.getContractAddress());
            return switchContract;
        }
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configureTimeouts(builder);
        return builder.build();
    }

    private void configureTimeouts(OkHttpClient.Builder builder) {
        long tos = 800000L;
        builder.connectTimeout(tos, TimeUnit.SECONDS);
        builder.readTimeout(tos, TimeUnit.SECONDS);  // Sets the socket timeout too
        builder.writeTimeout(tos, TimeUnit.SECONDS);
    }

}