package com.melloware;

import java.security.Principal;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/admin")
@RolesAllowed({ "${role.api.admin}" })
@Tag(name = "Admin Resource", description = "Only API Admin role can access this resource.")
public class AdminResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String admin(@Context SecurityContext sec) {
        Principal user = sec.getUserPrincipal();
        String name = user != null ? user.getName() : "anonymous";
        return "Administrator Resource: User " + name.toUpperCase() + " you are secured with "
                + sec.getAuthenticationScheme().toUpperCase() + " authentication.";
    }
}
