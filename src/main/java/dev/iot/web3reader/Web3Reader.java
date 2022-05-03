package dev.iot.web3reader;

import dev.iot.Switch;
import dev.iot.pi.AbstractPiController;
import dev.iot.pi.LEDState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(
        value="web3.module.enabled",
        havingValue = "true")
public class Web3Reader {

    private final AbstractPiController piController;
    private final Switch switchContract;
    private final Web3j httpWeb3j;
    private BigInteger lastBlock;
    private LEDState ledState;

    public Web3Reader(AbstractPiController piController, Switch switchContract, Web3j httpWeb3j) throws GeneralSecurityException, IOException {
        this.piController = piController;
        this.switchContract = switchContract;
        this.httpWeb3j = httpWeb3j;
    }

    @PostConstruct
    public void init() {
        lastBlock = getCurrentBlock();
    }

    @Scheduled(fixedDelay = 2000)
    public void readData() throws Exception {
        readEventsFromContract(lastBlock, getCurrentBlock());
        switchLed();
    }

    public void readEventsFromContract(BigInteger from, BigInteger to) throws IOException {
        log.debug("Read events from " + from + " to " + to + " last block " + lastBlock);
        EthFilter eventFilter = new EthFilter(DefaultBlockParameter.valueOf(from), DefaultBlockParameter.valueOf(to), switchContract.getContractAddress());
        String encodedEventSignature = EventEncoder.encode(Switch.SWITCHTURNED_EVENT);
        eventFilter.addSingleTopic(encodedEventSignature);
        Request<?, EthLog> resReg = httpWeb3j.ethGetLogs(eventFilter);
        List<EthLog.LogResult> regLogs = resReg.send().getLogs();
        LEDState newState = ledState;
        if (regLogs != null) {
            for (int i = 0; i < regLogs.size(); i++) {
                Log lastLogEntry = ((EthLog.LogObject) regLogs.get(i));
                List<String> ethLogTopics = lastLogEntry.getTopics();
                String address = ethLogTopics.get(1);
                String state = ethLogTopics.get(2).substring(65);
                if (lastLogEntry.getBlockNumber().compareTo(lastBlock) > 0) {
                    log.debug("user | switch : | 0x" + address + " | " + state);
                    newState = LEDState.values()[Integer.parseInt(state)];
                } else {
                    log.error("received old block for " + address + " | " + state + " at " + lastLogEntry.getBlockNumber() + " but last block is " + lastBlock);
                }
            }
            if (newState != ledState) {
                piController.switchLed(newState);
                ledState = newState;
            }
        } else {
            log.debug("No events found for Blocks " + from + " to " + to);
        }
        lastBlock = to;
    }

    private void switchLed() {
        if (ledState == LEDState.BLINK) {
            piController.switchLed(ledState);
        } else {
            log.debug("No action necessary in state " + ledState + ".");
        }
    }

    public BigInteger getCurrentBlock() {
        try {
            return httpWeb3j.ethBlockNumber().send().getBlockNumber();
        } catch (Exception e) {
            log.error("Cannot read current block number: {}", e.getMessage());
            return BigInteger.ZERO;
        }
    }

}
