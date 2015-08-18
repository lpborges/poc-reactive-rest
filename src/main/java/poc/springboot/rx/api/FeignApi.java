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

import org.glassfish.jersey.server.ManagedAsync;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import poc.springboot.rx.clients.ProductClient;
import poc.springboot.rx.domain.Product;

/**
* Feign test API
*
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/
@Path("feign")
@Api(value = "feign")
@Produces(MediaType.APPLICATION_JSON)
public class FeignApi {

    @Autowired
    private ProductClient productClient;

    @GET
    @Path("/products/{id}")
    @ApiOperation(value="Get a product by id with a feign client")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Product.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @ManagedAsync
    public void getProduct(@Suspended final AsyncResponse asyncResponse,
            @ApiParam(value = "Product id") @PathParam(value="id") @NotNull long id,
            @ApiParam(value = "UTM to apply discounts") @QueryParam(value="utm") String utm,
            @ApiParam(value = "In seconds. Simulate a IO time on productService for test.</br>"
                + "Note that if this value is greater than 10s, Hystrix will break the producService circuit")
                @QueryParam(value="productDelay") @DefaultValue("0") Integer productDelay,
            @ApiParam(value = "In seconds. Simulate a IO time on priceService for test.</br>"
                + "Note that if this value is greater than 10s, Hystrix will break the priceService circuit")
                @QueryParam(value="priceDelay") @DefaultValue("0") Integer priceDelay) {

        try {
            final Product product = productClient.getProduct(id, utm, productDelay, priceDelay);
            asyncResponse.resume(product);
        }
        catch(final Exception e){
            System.out.println(e);
            asyncResponse.resume(e);
        }
    }
}
