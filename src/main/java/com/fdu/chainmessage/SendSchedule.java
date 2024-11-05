package com.fdu.chainmessage;

import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SendSchedule {
    private final SimulateChainMessage simulateChainMessage;

    public SendSchedule(SimulateChainMessage simulateChainMessage) {
        this.simulateChainMessage = simulateChainMessage;
    }
    
    @Scheduled(fixedRate = 1000)
    public void testSyn() {
        System.out.println("testSyn");
        ChainEventMessage chainEventMessage = new ChainEventMessage();
        String uuid = UUID.randomUUID().toString();
        Long curTime = System.currentTimeMillis();
        chainEventMessage.setKey(uuid);
        chainEventMessage.setChainType("fabric");
        chainEventMessage.setChannelName("test_channel");
        chainEventMessage.setValue(uuid);
        chainEventMessage.setOperationType((byte) 1);
        chainEventMessage.setUpdateTime(curTime);
        simulateChainMessage.sendChainEventMessage(chainEventMessage);
    }   
}
