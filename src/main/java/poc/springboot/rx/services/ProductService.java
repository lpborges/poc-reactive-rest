package poc.springboot.rx.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.command.ObservableResult;

import poc.springboot.rx.domain.Offer;
import poc.springboot.rx.domain.Product;
import poc.springboot.rx.domain.ProductVariation;
import rx.Observable;

/**
 * Service that emulate a REST Client for get a product by id
 *
 * @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
 */

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public static final int DEFAULT_PRICE = 100;

    private static final long[] VARIATIONS_ID = { 1, 2, 3, 4, 5 };

    private static final long[] SELLERS_ID = { 1, 2, 3 };

    /**
     * Simulate get a product by id
     *
     * @param id the product id
     * @param delay delay simulate a IO time for test
     * @return
     */
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
    })
    public Observable<Product> getProductById(long id, int delay) {

        // Encabsulate on a Observable HystrixCommand
        return new ObservableResult<Product>() {

            @Override
            public Product invoke() {
                final long start = System.currentTimeMillis();

                LOGGER.debug("Begin getProductById({})", id);

                if(delay > 0){
                    // Simulate a IO time
                    try {
                        TimeUnit.SECONDS.sleep(delay);
                    } catch (final InterruptedException e) {
                        LOGGER.error("Sleep interrupted", e);
                    }
                }

                final Product product = buildProduct(id);

                LOGGER.debug("End getProductById({}) in {}ms", id, (System.currentTimeMillis() - start));

                return product;
            }
        };
    }

    // Build a product to simulate a request
    private Product buildProduct(long id) {
        final Product product = new Product();
        product.setId(id);
        product.setName("product_" + id);
        product.setVariations(buildVariations());
        return product;
    }

    private List<ProductVariation> buildVariations() {
        return Arrays.stream(VARIATIONS_ID).mapToObj(i -> {
            final ProductVariation pv = new ProductVariation();
            pv.setId(i);
            pv.setName("variation_" + i);
            pv.setOffers(buildOffers(i));
            return pv;
        }).collect(Collectors.toList());
    }

    private List<Offer> buildOffers(long idSku) {
        return Arrays.stream(SELLERS_ID).mapToObj(i -> {
            final Offer offer = new Offer();
            offer.setId(i);
            offer.setPrice(new BigDecimal(DEFAULT_PRICE));
            offer.setSellerId(i);
            offer.setSellerName("seller_" + i);
            offer.setName("sku_"+idSku+"_offer_"+i);
            return offer;
        }).collect(Collectors.toList());
    }
}
