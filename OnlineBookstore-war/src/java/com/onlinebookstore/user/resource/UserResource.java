package com.onlinebookstore.user.resource;

import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.security.Secured;
import com.onlinebookstore.user.dto.AdminUserRequest;
import com.onlinebookstore.user.dto.UserResponse;
import com.onlinebookstore.user.service.UserService;

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

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured({"ADMIN", "MANAGER"})
public class UserResource {

    @Inject
    private UserService userService;

    @GET
    public Response getUsers(
            @QueryParam("q") String search,
            @QueryParam("role") String role
    ) {
        ApiResponse<List<UserResponse>> result = userService.getUsers(search, role);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Integer id) {
        ApiResponse<UserResponse> result = userService.getUserById(id);
        return Response.ok(result).build();
    }

    @POST
    public Response createUser(@Valid AdminUserRequest request) {
        ApiResponse<UserResponse> result = userService.createUser(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Integer id, @Valid AdminUserRequest request) {
        ApiResponse<UserResponse> result = userService.updateUser(id, request);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateUserStatus(@PathParam("id") Integer id, @QueryParam("active") Boolean active) {
        ApiResponse<UserResponse> result = userService.updateUserStatus(id, active);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Integer id) {
        ApiResponse<String> result = userService.deleteUser(id);
        return Response.ok(result).build();
    }
}
