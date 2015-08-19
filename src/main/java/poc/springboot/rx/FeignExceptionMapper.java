package poc.springboot.rx;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import feign.FeignException;

/**
* @author <a href="mailto:leandropg@ciandt.com">Leandro de Paula Borges</a>
*/
public class FeignExceptionMapper implements ExceptionMapper<FeignException> {

    @Override
    public Response toResponse(FeignException exception) {
        return Response.status(500).build();
    }
}
