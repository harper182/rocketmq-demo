package cn.meixs.rocketmqdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RocketmqDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RocketmqDemoApplication.class, args);
	}
}
