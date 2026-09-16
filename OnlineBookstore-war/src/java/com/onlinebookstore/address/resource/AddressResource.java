package com.onlinebookstore.address.resource;

import com.onlinebookstore.address.dto.AddressResponse;
import com.onlinebookstore.address.dto.CreateAddressRequest;
import com.onlinebookstore.address.dto.UpdateAddressRequest;
import com.onlinebookstore.address.service.IAddressService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.security.Secured;
import com.onlinebookstore.common.security.UserPrincipal;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/addresses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AddressResource {

    @Inject
    private IAddressService addressService;

    @POST
    @Secured
    public Response createAddress(@Context SecurityContext securityContext, @Valid CreateAddressRequest request) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<AddressResponse> result = addressService.createAddress(principal.getUserId(), request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Secured
    public Response getAddresses(@Context SecurityContext securityContext) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<List<AddressResponse>> result = addressService.getAddressByUser(principal.getUserId());
        return Response.ok(result).build();
    }

    @GET
    @Path("/default")
    @Secured
    public Response getDefaultAddress(@Context SecurityContext securityContext) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<AddressResponse> result = addressService.getAddressByStatus(principal.getUserId(), true);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{addressId}")
    @Secured
    public Response getAddressById(@Context SecurityContext securityContext, @PathParam("addressId") Integer addressId) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<AddressResponse> result = addressService.getAddressById(principal.getUserId(), addressId);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{addressId}")
    @Secured
    public Response updateAddress(
            @Context SecurityContext securityContext,
            @PathParam("addressId") Integer addressId,
            @Valid UpdateAddressRequest request
    ) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<AddressResponse> result = addressService.updateAddress(principal.getUserId(), addressId, request);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{addressId}/default")
    @Secured
    public Response setDefaultAddress(
            @Context SecurityContext securityContext,
            @PathParam("addressId") Integer addressId
    ) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<AddressResponse> result = addressService.setDefaultAddress(principal.getUserId(), addressId);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{addressId}")
    @Secured
    public Response deleteAddress(
            @Context SecurityContext securityContext,
            @PathParam("addressId") Integer addressId
    ) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<String> result = addressService.deleteAddress(principal.getUserId(), addressId);
        return Response.ok(result).build();
    }
}
