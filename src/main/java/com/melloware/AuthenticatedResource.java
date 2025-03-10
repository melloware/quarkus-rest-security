package com.melloware;

import java.security.Principal;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/authenticated")
@Authenticated
@Tag(name = "Authenticated Resource", description = "As long as you are authenticated you can access this resource regardless of your role.")
public class AuthenticatedResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String secure(@Context SecurityContext sec) {
        Principal user = sec.getUserPrincipal();
        String name = user != null ? user.getName() : "anonymous";
        return "Authenticated Resource: User " + name.toUpperCase() + " you are secured with "
                + sec.getAuthenticationScheme().toUpperCase() + " authentication.";
    }
}
