package poc.springboot.rx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/
@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableFeignClients
public class PocReactiveRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocReactiveRestApplication.class, args);
    }
}
