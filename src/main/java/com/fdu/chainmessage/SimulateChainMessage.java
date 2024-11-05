package com.fdu.chainmessage;

import java.security.Provider;
import java.util.*;
import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import jakarta.annotation.PostConstruct;

@Service
public class SimulateChainMessage {
    //"10.176.24.18:8081",
    private final String[] proxyAr = {  "10.176.24.35:8081" };
    private final String[] topicAr = { "syn", "syn" };
    private List<Producer> producerList = new ArrayList<>();
    ClientServiceProvider provider;
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(Kryo::new);

    @PostConstruct
    public void initProducer() throws ClientException {
        provider = ClientServiceProvider.loadService();

        for (int i = 0; i < proxyAr.length; i++) {
            String endpoint = proxyAr[i];
            String topic = topicAr[i];

            // 配置ClientConfiguration
            ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(endpoint);
            ClientConfiguration configuration = builder.build();

            // 创建Producer并添加到集合中
            Producer producer = provider.newProducerBuilder()
                    .setClientConfiguration(configuration)
                    .build();
            producerList.add(producer);
            System.out.println("success init producer: " + producer);

        }
    }

    // 通过syntopic发送区块链事件消息
    public void sendChainEventMessage(ChainEventMessage chainEventMessage) {
        Kryo kryo = kryoThreadLocal.get();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); // 重用字节输出流
        Output output = new Output(byteOut); // 重用 Kryo 的 Output 对象
        kryo.writeObject(output, chainEventMessage);
        output.flush();

        byte[] serializedBytes = byteOut.toByteArray(); // 获取序列化后的字节数组
       
        for (int i = 0; i < producerList.size(); i++) {
            Message message = provider.newMessageBuilder()
                    .setTopic(topicAr[i])
                    .setTag(chainEventMessage.getChainType() + "_" + chainEventMessage.getChannelName())
                    .setBody(serializedBytes)
                    .build();
            try {
                System.out.println("sendChainEventMessage: " + chainEventMessage);
                // 发送消息，需要关注发送结果，并捕获失败等异常。
                SendReceipt sendReceipt = producerList.get(i).send(message);
                System.out.println("sendReceipt: " + sendReceipt);

            } catch (ClientException e) {
                System.out.println("sendChainEventMessage error: " + e.getMessage());
            }
        }

    }

}
