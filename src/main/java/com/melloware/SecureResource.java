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

@Path("/api/secure")
@RolesAllowed({ "${role.api.admin}", "${role.api.user}" })
@Tag(name = "Secure Resource", description = "Both API Admin and User roles can access this resource.")
public class SecureResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String secure(@Context SecurityContext sec) {
        Principal user = sec.getUserPrincipal();
        String name = user != null ? user.getName() : "anonymous";
        return "Secure Resource: User " + name.toUpperCase() + " you are secured with "
                + sec.getAuthenticationScheme().toUpperCase() + " authentication.";
    }
}
