package com.onlinebookstore.order.resource;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.security.Secured;
import com.onlinebookstore.common.security.UserPrincipal;
import com.onlinebookstore.order.dto.CreateOrderRequest;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.dto.UpdateOrderStatusRequest;
import com.onlinebookstore.order.service.IOrderService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private IOrderService orderService;

    @POST
    @Secured
    public Response createOrder(@Context SecurityContext securityContext, @Valid CreateOrderRequest request) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<OrderResponse> result = orderService.createOrder(principal.getUserId(), request);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Secured
    public Response getUserOrders(@Context SecurityContext securityContext) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<List<OrderResponse>> result = orderService.getUserOrders(principal.getUserId());
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Secured
    public Response getOrderById(@Context SecurityContext securityContext, @PathParam("id") Integer id) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        boolean isAdmin = securityContext.isUserInRole("admin");
        ApiResponse<OrderResponse> result = orderService.getOrderById(principal.getUserId(), id, isAdmin);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/code/{orderCode}")
    @Secured
    public Response getOrderByCode(@Context SecurityContext securityContext, @PathParam("orderCode") String orderCode) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        boolean isAdmin = securityContext.isUserInRole("admin");
        ApiResponse<OrderResponse> result = orderService.getOrderByCode(principal.getUserId(), orderCode, isAdmin);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.NOT_FOUND).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/cancel")
    @Secured
    public Response cancelOrder(@Context SecurityContext securityContext, @PathParam("id") Integer id) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        boolean isAdmin = securityContext.isUserInRole("admin");
        ApiResponse<OrderResponse> result = orderService.cancelOrder(principal.getUserId(), id, isAdmin);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/admin")
    @Secured({"admin"})
    public Response getAllOrders(@QueryParam("status") String status) {
        ApiResponse<List<OrderResponse>> result = orderService.getAllOrders(status);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/admin/{id}/status")
    @Secured({"admin"})
    public Response updateOrderStatus(@PathParam("id") Integer id, @Valid UpdateOrderStatusRequest request) {
        ApiResponse<OrderResponse> result = orderService.updateOrderStatus(id, request);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }
}
