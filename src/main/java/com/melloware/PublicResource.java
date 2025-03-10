package com.melloware;

import java.security.Principal;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/public")
@PermitAll
@Tag(name = "Public Resource", description = "Any user can access this resource.")
public class PublicResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String unrestricted(@Context SecurityContext sec) {
        Principal user = sec.getUserPrincipal();
        String name = user != null ? user.getName() : "anonymous";
        return "Public Resource: User " + name.toUpperCase() + " accessed a public resource.";
    }
}
