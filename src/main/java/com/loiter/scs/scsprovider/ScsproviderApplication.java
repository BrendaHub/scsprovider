package com.loiter.scs.scsprovider;

import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
// spring-cloud-starter-stream-rocketmq 的配置
/**
 * org.springframework.cloud.stream.binder.Binder是Spring Cloud对消息容器的抽象，
 * 不同的消息容器不同的实现， 通过它可以屏蔽各消息窗口的内部细节
 */
@EnableBinding(Source.class)
public class ScsproviderApplication {

	public static void main(String[] args) {
//		SpringApplication.run(ScsproviderApplication.class, args);
		new SpringApplicationBuilder(ScsproviderApplication.class).web(WebApplicationType.NONE).run(args);
	}

	// Spring boot 的一个基础类
	@Bean
	public CommandLineRunner commandLineRunner() {
		return new CommandLineRunner() {
			// 实例化Sources 对象
			@Autowired
			private Source source;
			// 会顺序执行里面的代码
			@Override
			public void run(String... args) throws Exception {
				// 1 、先构造Message
				// 2、 使用MessageChannel进行消息发送， 并发送到RockerMQ
				// 2.1、 使用EnableBinding 代替原先的DirectChannel
				Message message = null;//MessageBuilder.withPayload("simple message").build();
//				DirectChannel channel = new DirectChannel();
				for(int i = 0 ; i < 100 ;i ++) {
					message = MessageBuilder.withPayload("simple message > " + i ).build();
					source.output().send(message);// 这里还是不能得到MQ对应的topic的名称，
				}

			}
		};
	}
}

@Component
class SourceProducer{

	@Autowired
	private Source source;

	public void sendMessage(String msg) {
		String payload = msg;
		Map<String,Object> headers = new HashMap<>();
		headers.put(MessageConst.PROPERTY_TAGS, "testTag");
		MessageHeaders messageHeaders = new MessageHeaders(headers);
		Message<String> message = MessageBuilder.withPayload(payload).copyHeaders(headers).build();
		this.source.output().send(message);
	}
}