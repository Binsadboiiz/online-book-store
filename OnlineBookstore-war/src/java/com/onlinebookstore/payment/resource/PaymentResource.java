package com.onlinebookstore.payment.resource;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.security.Secured;
import com.onlinebookstore.common.security.UserPrincipal;
import com.onlinebookstore.payment.dto.CreatePaymentRequest;
import com.onlinebookstore.payment.dto.PaymentResponse;
import com.onlinebookstore.payment.dto.UpdatePaymentStatusRequest;
import com.onlinebookstore.payment.service.IPaymentService;

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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private IPaymentService paymentService;

    @POST
    @Secured
    public Response createPayment(@Context SecurityContext securityContext, @Valid CreatePaymentRequest request) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<PaymentResponse> result = paymentService.createPayment(principal.getUserId(), request);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Secured
    public Response getUserPayments(@Context SecurityContext securityContext) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<List<PaymentResponse>> result = paymentService.getUserPayments(principal.getUserId());
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Secured
    public Response getPaymentById(@Context SecurityContext securityContext, @PathParam("id") Integer id) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        boolean isAdmin = securityContext.isUserInRole("admin");
        ApiResponse<PaymentResponse> result = paymentService.getPaymentById(principal.getUserId(), id, isAdmin);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/transaction/{transactionCode}")
    @Secured
    public Response getPaymentByTransactionCode(@Context SecurityContext securityContext, @PathParam("transactionCode") String transactionCode) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        boolean isAdmin = securityContext.isUserInRole("admin");
        ApiResponse<PaymentResponse> result = paymentService.getPaymentByTransactionCode(principal.getUserId(), transactionCode, isAdmin);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.NOT_FOUND).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/order/{orderId}")
    @Secured
    public Response getPaymentsByOrderId(@Context SecurityContext securityContext, @PathParam("orderId") Integer orderId) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        boolean isAdmin = securityContext.isUserInRole("admin");
        ApiResponse<List<PaymentResponse>> result = paymentService.getPaymentsByOrderId(principal.getUserId(), orderId, isAdmin);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/admin")
    @Secured({"admin"})
    public Response getAllPayments(@QueryParam("status") String status) {
        ApiResponse<List<PaymentResponse>> result = paymentService.getAllPayments(status);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/admin/{id}/status")
    @Secured({"admin"})
    public Response updatePaymentStatus(@PathParam("id") Integer id, @Valid UpdatePaymentStatusRequest request) {
        ApiResponse<PaymentResponse> result = paymentService.updatePaymentStatus(id, request);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/admin/{id}")
    @Secured({"admin"})
    public Response deletePayment(@Context SecurityContext securityContext, @PathParam("id") Integer id) {
        boolean isAdmin = securityContext.isUserInRole("admin");
        ApiResponse<String> result = paymentService.deletePayment(id, isAdmin);
        if (!result.isSuccess()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        return Response.ok(result).build();
    }
}

