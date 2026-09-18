package com.onlinebookstore.book.resource;

import com.onlinebookstore.book.dto.AuthorRequest;
import com.onlinebookstore.book.dto.AuthorResponse;
import com.onlinebookstore.book.service.AuthorService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.security.Secured;

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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @Inject
    private AuthorService authorService;

    @GET
    public Response getAuthors(@QueryParam("q") String search) {
        ApiResponse<List<AuthorResponse>> result = authorService.getAuthors(search);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    public Response getAuthorById(@PathParam("id") Integer id) {
        ApiResponse<AuthorResponse> result = authorService.getAuthorById(id);
        return Response.ok(result).build();
    }

    @POST
    @Secured({"MANAGER", "ADMIN"})
    public Response createAuthor(@Valid AuthorRequest request) {
        ApiResponse<AuthorResponse> result = authorService.createAuthor(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{id}")
    @Secured({"MANAGER", "ADMIN"})
    public Response updateAuthor(@PathParam("id") Integer id, @Valid AuthorRequest request) {
        ApiResponse<AuthorResponse> result = authorService.updateAuthor(id, request);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured({"MANAGER", "ADMIN"})
    public Response deleteAuthor(@PathParam("id") Integer id) {
        ApiResponse<String> result = authorService.deleteAuthor(id);
        return Response.ok(result).build();
    }
}
