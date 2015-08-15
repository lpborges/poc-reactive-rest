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
import rx.Observable;

/**
* Service that emulate a REST Client for get discount offers given a UTM
*
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/
@Service
public class PriceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceService.class);

    public static final int DISCOUNT_PRICE = 50;

    private static final long[] SELLERS_ID = { 1, 2, 3 };

    /**
     * Simulate to get a list of sku offers with discount for a utm
     *
     * @param idSku the sku (product variation) to search offers
     * @param utm the utm to find the discounts
     * @param delay simulate a IO time for test
     * @return a observable for the discounts offers
     */
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
    })
    public Observable<List<Offer>> getDiscountOffersByUtm(long idSku, String utm, int delay) {

        // Encabsulate on a Observable HystrixCommand
        return new ObservableResult<List<Offer>>() {

            @Override
            public List<Offer> invoke() {
                final long start = System.currentTimeMillis();

                LOGGER.debug("Begin getDiscountOffersByUtm({}, {})", idSku, utm);

                if(delay > 0){
                    // Simulate a IO time
                    try {
                        TimeUnit.SECONDS.sleep(delay);
                    } catch (final InterruptedException e) {
                        LOGGER.error("Sleep interrupted", e);
                    }
                }
                final List<Offer> offers = buildOffers(idSku);

                LOGGER.debug("End getDiscountOffersByUtm({}, {}) in {}ms", idSku, utm, (System.currentTimeMillis() - start));

                return offers;
            }
        };
    }

    // Build a list of offers to simulate a request
    private List<Offer> buildOffers(long idSku) {

        return Arrays.stream(SELLERS_ID).mapToObj(i -> {
            final Offer offer = new Offer();
            offer.setId(i);
            offer.setPrice(new BigDecimal(DISCOUNT_PRICE));
            offer.setSellerId(i);
            offer.setSellerName("seller_" + i);
            offer.setName("sku_"+idSku+"_offer_"+i);
            return offer;
        }).collect(Collectors.toList());
    }
}
