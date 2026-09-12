package com.onlinebookstore.cart.resource;

import com.onlinebookstore.cart.dto.AddToCartRequest;
import com.onlinebookstore.cart.dto.CartResponse;
import com.onlinebookstore.cart.dto.UpdateCartItemRequest;
import com.onlinebookstore.cart.service.ICartService;
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

@Path("/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @Inject
    private ICartService cartService;

    @GET
    @Secured
    public Response getCart(@Context SecurityContext securityContext) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<CartResponse> result = cartService.getCartByUserId(principal.getUserId());
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @POST
    @Path("/items")
    @Secured
    public Response addToCart(@Context SecurityContext securityContext, @Valid AddToCartRequest request) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<CartResponse> result = cartService.addToCart(principal.getUserId(), request);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @PUT
    @Path("/items/{itemId}")
    @Secured
    public Response updateCartItem(
            @Context SecurityContext securityContext,
            @PathParam("itemId") Integer itemId,
            @Valid UpdateCartItemRequest request
    ) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<CartResponse> result = cartService.updateCartItem(principal.getUserId(), itemId, request);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/items/{itemId}")
    @Secured
    public Response removeCartItem(
            @Context SecurityContext securityContext,
            @PathParam("itemId") Integer itemId
    ) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<CartResponse> result = cartService.removeCartItem(principal.getUserId(), itemId);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @DELETE
    @Secured
    public Response clearCart(@Context SecurityContext securityContext) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<String> result = cartService.clearCart(principal.getUserId());
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }
}
