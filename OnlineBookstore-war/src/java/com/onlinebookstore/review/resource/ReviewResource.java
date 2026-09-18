package com.onlinebookstore.review.resource;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.security.Secured;
import com.onlinebookstore.common.security.UserPrincipal;
import com.onlinebookstore.review.dto.CreateReviewRequest;
import com.onlinebookstore.review.dto.ReviewResponse;
import com.onlinebookstore.review.dto.ReviewSummaryResponse;
import com.onlinebookstore.review.service.ReviewService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    @Inject
    private ReviewService reviewService;

    @GET
    @Path("/book/{bookId}")
    public Response getReviewsByBookId(@PathParam("bookId") Integer bookId) {
        ApiResponse<List<ReviewResponse>> result = reviewService.getApprovedReviews(bookId);
        return Response.ok(result).build();
    }

    @GET
    @Path("/book/{bookId}/summary")
    public Response getReviewSummaryByBookId(@PathParam("bookId") Integer bookId) {
        ApiResponse<ReviewSummaryResponse> result = reviewService.getReviewSummary(bookId);
        return Response.ok(result).build();
    }

    @POST
    @Secured
    public Response createReview(@Context SecurityContext securityContext, @Valid CreateReviewRequest request) {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        ApiResponse<ReviewResponse> result = reviewService.createReview(principal.getUserId(), request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}
