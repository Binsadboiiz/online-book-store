package com.onlinebookstore.book.resource;

import com.onlinebookstore.book.dto.BookRequest;
import com.onlinebookstore.book.dto.BookResponse;
import com.onlinebookstore.book.service.BookService;
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

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    private BookService bookService;

    @GET
    public Response getBooks(
            @QueryParam("q") String search,
            @QueryParam("categoryId") Integer categoryId,
            @QueryParam("authorId") Integer authorId,
            @QueryParam("publisherId") Integer publisherId
    ) {
        ApiResponse<List<BookResponse>> result = bookService.getBooks(search, categoryId, authorId, publisherId);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Integer id) {
        ApiResponse<BookResponse> result = bookService.getBookById(id);
        return Response.ok(result).build();
    }

    @POST
    @Secured({"admin"})
    public Response createBook(@Valid BookRequest request) {
        ApiResponse<BookResponse> result = bookService.createBook(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{id}")
    @Secured({"admin"})
    public Response updateBook(@PathParam("id") Integer id, @Valid BookRequest request) {
        ApiResponse<BookResponse> result = bookService.updateBook(id, request);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured({"admin"})
    public Response deleteBook(@PathParam("id") Integer id) {
        ApiResponse<String> result = bookService.deleteBook(id);
        return Response.ok(result).build();
    }
}
