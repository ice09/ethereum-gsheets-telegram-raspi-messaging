package dev.iot.web3reader;

import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;

@Slf4j
public class CredentialHolder {

    private final Bip32ECKeyPair masterKeypair;

    public CredentialHolder(Bip32ECKeyPair masterKeypair) {
        this.masterKeypair = masterKeypair;
    }

    public Credentials deriveChildKeyPair(int index) {
        final int[] path = {44 | 0x80000000, 60 | 0x80000000, 0 | 0x80000000, 0, index};
        Bip32ECKeyPair childKeypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
        Credentials credentials = Credentials.create(childKeypair);
        log.info("Derived Address from Mnemonic: {}", credentials.getAddress());
        return credentials;
    }
    
}