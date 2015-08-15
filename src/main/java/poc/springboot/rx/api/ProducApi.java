package poc.springboot.rx.api;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import poc.springboot.rx.domain.Product;
import poc.springboot.rx.services.PriceService;
import poc.springboot.rx.services.ProductService;
import rx.Observable;
import rx.Observer;

/**
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/

@Path("products")
@Api(value = "products")
@Produces(MediaType.APPLICATION_JSON)
public class ProducApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducApi.class);

    public static final long SELLER_WITH_DISCOUNTS = 1;

    @Autowired
    private ProductService productService;

    @Autowired
    private PriceService priceService;

    @GET
    @Path("/{id}")
    @ApiOperation(value="Get a product by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Product.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void getProduct(@Suspended final AsyncResponse asyncResponse,
            @ApiParam(value = "Product id") @PathParam(value="id") @NotNull long id,
            @ApiParam(value = "UTM to apply discounts") @QueryParam(value="utm") String utm,
            @ApiParam(value = "In seconds. Simulate a IO time on productService for test.</br>"
                + "Note that if this value is greater than 10s, Hystrix will break the producService circuit")
                @QueryParam(value="productDelay") @DefaultValue("0") Integer productDelay,
            @ApiParam(value = "In seconds. Simulate a IO time on priceService for test.</br>"
                + "Note that if this value is greater than 10s, Hystrix will break the priceService circuit")
                @QueryParam(value="priceDelay") @DefaultValue("0") Integer priceDelay) {

        final long start = System.currentTimeMillis();

        LOGGER.info("Get product with id "+id+" using utm="+utm);

        if(productDelay == null){
            productDelay = 0;
        }

        if(priceDelay == null){
            priceDelay = 0;
        }

        Observable<Product> product = null;

        if(utm == null){
            // dont apply disccount
            product = productService.getProductById(id, productDelay);
        }
        else {
            // apply discount
            product = getProductWithDiscount(id, utm, productDelay, priceDelay);
        }

        //.subscribeOn(Schedulers.io())
        product.subscribe(new Observer<Product>() {
             @Override
             public void onCompleted() {
             }

             @Override
             public void onError(Throwable e) {
                 LOGGER.error("Error on request product", e);
                 LOGGER.info("Total time with error: {}ms", (System.currentTimeMillis() - start));
                 asyncResponse.resume(e);
             }

             @Override
             public void onNext(Product product) {
                LOGGER.info("Found the product "+product);
                LOGGER.info("Total time: {}ms", (System.currentTimeMillis() - start));
                asyncResponse.resume(product);
             }
        });
    }

    /**
     * REACTIVE MAGIC HAPPENS HERE!!!
     *
     * Get a product then apply discount for all variation in parallel
     *
     * @param id the product id
     * @param utm the utm to find the discounts
     * @param productDelay simulate a IO time on productService for test
     * @param priceDelay simulate a IO time on serviceService for test
     * @return a observable for the produc with discounts applied
     */
    private Observable<Product> getProductWithDiscount(final long id, final String utm, int productDelay, int priceDelay) {

        final Observable<Product> rxProduct = productService.getProductById(id, productDelay);

        // process the product
        return rxProduct.flatMap(p -> {
            // process each product variation
            return Observable.from(p.getVariations())
                    // filter to process just variation with discounts seller offers
                    .filter(v -> v.getOffers().stream().anyMatch(o -> o.getSellerId() == SELLER_WITH_DISCOUNTS) )
                    //process the filtered variations
                    .flatMap(v -> {
                        // get the offers with discounts by utm for the variation
                        return priceService.getDiscountOffersByUtm(v.getId(), utm, priceDelay)
                                // process the discounts
                                .flatMapIterable(utmOffers -> utmOffers).flatMap(utmOffer -> {
                                    // set the discount price on each offer that has the discounts seller id
                                    v.getOffers().stream()
                                        .filter(o -> (o.getId() == utmOffer.getId() && o.getSellerId() == SELLER_WITH_DISCOUNTS) )
                                        .forEach(o -> {
                                            o.setPrice(utmOffer.getPrice());
                                        });
                                return Observable.just(v);
                        });
            })
            // after the last variation process return the product Observable
            .last().map(v -> p);
         });
    }
}
