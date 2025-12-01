package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/example")
public class ExampleResource {

    @GET
    public Response getExample() {
        return Response.ok("Hello!").build();
    }
}