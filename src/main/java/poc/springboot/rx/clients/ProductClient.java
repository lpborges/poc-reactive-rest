package poc.springboot.rx.clients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import poc.springboot.rx.domain.Product;

/**
* Interface for the Product API
*
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/

@FeignClient(url="localhost:8080/api")
public interface ProductClient {

    @RequestMapping(method = RequestMethod.GET, value = "/products/{idProduct}", consumes = "application/json")
    public Product getProduct(
            @PathVariable("idProduct") long idProduct,
            @RequestParam("utm") String utm,
            @RequestParam("priceDelay") Integer productDelay,
            @RequestParam("priceDelay") Integer priceDelay
        );
}
