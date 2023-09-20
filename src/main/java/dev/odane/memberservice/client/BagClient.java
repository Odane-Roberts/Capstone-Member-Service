package dev.odane.memberservice.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="bag-service", url = "${spring.application.config.bag-url}")
public interface BagClient {

}
