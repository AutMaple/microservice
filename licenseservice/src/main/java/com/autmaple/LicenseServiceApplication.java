package com.autmaple;

import com.autmaple.events.model.OrganizationChangeModel;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
@EnableFeignClients
public class LicenseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LicenseServiceApplication.class, args);
    }

    @Bean
    public Consumer<OrganizationChangeModel> consumer() {
        return (model) -> System.out.println(model.toString());
    }

//    @Bean
//    public Supplier<OrganizationChangeModel> supplier() {
//        return () -> new OrganizationChangeModel("Organization", "Modify", "hahfshh", "1092201-fsjk");
//    }

    @Bean
    ApplicationRunner runner(StreamBridge bridge) {
        return args -> {
            OrganizationChangeModel organizationModel = new OrganizationChangeModel("change", "modify", "autmaple", "991009-qc");
            bridge.send("test", organizationModel);
        };
    }
}
