package com.ecommerce.registry.service_registry;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }

    @Bean
    public static BeanPostProcessor eurekaJerseyConfigPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName)
                    throws BeansException {

                if (bean instanceof ResourceConfig resourceConfig) {
                    resourceConfig.property(
                            "jersey.config.server.wadl.disableWadl",
                            true
                    );
                }

                return bean;
            }
        };
    }
}