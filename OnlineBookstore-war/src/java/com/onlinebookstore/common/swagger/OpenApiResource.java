package com.onlinebookstore.common.swagger;

import com.onlinebookstore.common.security.Secured;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/openapi.json")
public class OpenApiResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOpenApiSpec() {
        String jsonSpec = """
        {
          "openapi": "3.0.3",
          "info": {
            "title": "Online Bookstore REST API",
            "description": "Comprehensive RESTful APIs for Online Bookstore application built with Jakarta EE, JAX-RS, and JSF.",
            "version": "1.0.0",
            "contact": {
              "name": "Development Team",
              "email": "dev@onlinebookstore.com"
            }
          },
          "servers": [
            {
              "url": "/OnlineBookstore-war/api",
              "description": "Application REST API Base Server"
            }
          ],
          "components": {
            "securitySchemes": {
              "BearerAuth": {
                "type": "http",
                "scheme": "bearer",
                "bearerFormat": "JWT"
              }
            },
            "schemas": {
              "BookRequest": {
                "type": "object",
                "required": ["title", "price", "stockQuantity"],
                "properties": {
                  "title": { "type": "string", "example": "Clean Code" },
                  "isbn": { "type": "string", "example": "978-0132350884" },
                  "price": { "type": "number", "example": 44.95 },
                  "discountPrice": { "type": "number", "example": 39.99 },
                  "stockQuantity": { "type": "integer", "example": 15 },
                  "description": { "type": "string" },
                  "coverImage": { "type": "string" },
                  "publishedYear": { "type": "integer", "example": 2008 },
                  "pages": { "type": "integer", "example": 464 },
                  "language": { "type": "string", "example": "English" },
                  "categoryId": { "type": "integer" },
                  "authorId": { "type": "integer" },
                  "publisherId": { "type": "integer" }
                }
              },
              "LoginRequest": {
                "type": "object",
                "required": ["username", "password"],
                "properties": {
                  "username": { "type": "string", "example": "admin" },
                  "password": { "type": "string", "example": "admin123" }
                }
              },
              "RegisterRequest": {
                "type": "object",
                "required": ["username", "email", "password"],
                "properties": {
                  "username": { "type": "string", "example": "john_doe" },
                  "email": { "type": "string", "example": "john@example.com" },
                  "password": { "type": "string", "example": "secret123" },
                  "fullName": { "type": "string", "example": "John Doe" },
                  "phone": { "type": "string", "example": "0912345678" }
                }
              }
            }
          },
          "paths": {
            "/books": {
              "get": {
                "summary": "Get catalog books",
                "description": "Fetch list of books with optional filters for keyword search, category, author, or publisher.",
                "tags": ["Books"],
                "parameters": [
                  { "name": "q", "in": "query", "description": "Search keyword", "schema": { "type": "string" } },
                  { "name": "categoryId", "in": "query", "description": "Filter by Category ID", "schema": { "type": "integer" } },
                  { "name": "authorId", "in": "query", "description": "Filter by Author ID", "schema": { "type": "integer" } },
                  { "name": "publisherId", "in": "query", "description": "Filter by Publisher ID", "schema": { "type": "integer" } }
                ],
                "responses": {
                  "200": { "description": "List of books retrieved successfully" }
                }
              },
              "post": {
                "summary": "Create a new book",
                "description": "Requires Manager security role.",
                "tags": ["Books"],
                "security": [{ "BearerAuth": [] }],
                "requestBody": {
                  "required": true,
                  "content": {
                    "application/json": { "schema": { "$ref": "#/components/schemas/BookRequest" } }
                  }
                },
                "responses": {
                  "201": { "description": "Book created successfully" },
                  "401": { "description": "Unauthorized" },
                  "403": { "description": "Forbidden - Manager access required" }
                }
              }
            },
            "/books/{id}": {
              "get": {
                "summary": "Get book by ID",
                "tags": ["Books"],
                "parameters": [
                  { "name": "id", "in": "path", "required": true, "schema": { "type": "integer" } }
                ],
                "responses": {
                  "200": { "description": "Book found" },
                  "404": { "description": "Book not found" }
                }
              },
              "put": {
                "summary": "Update existing book",
                "tags": ["Books"],
                "security": [{ "BearerAuth": [] }],
                "parameters": [
                  { "name": "id", "in": "path", "required": true, "schema": { "type": "integer" } }
                ],
                "requestBody": {
                  "required": true,
                  "content": {
                    "application/json": { "schema": { "$ref": "#/components/schemas/BookRequest" } }
                  }
                },
                "responses": {
                  "200": { "description": "Book updated" },
                  "404": { "description": "Book not found" }
                }
              },
              "delete": {
                "summary": "Delete book",
                "tags": ["Books"],
                "security": [{ "BearerAuth": [] }],
                "parameters": [
                  { "name": "id", "in": "path", "required": true, "schema": { "type": "integer" } }
                ],
                "responses": {
                  "200": { "description": "Book deleted" }
                }
              }
            },
            "/auth/login": {
              "post": {
                "summary": "User authentication login",
                "tags": ["Authentication"],
                "requestBody": {
                  "required": true,
                  "content": {
                    "application/json": { "schema": { "$ref": "#/components/schemas/LoginRequest" } }
                  }
                },
                "responses": {
                  "200": { "description": "Authentication successful with token" },
                  "401": { "description": "Invalid credentials" }
                }
              }
            },
            "/auth/register": {
              "post": {
                "summary": "Register new account",
                "tags": ["Authentication"],
                "requestBody": {
                  "required": true,
                  "content": {
                    "application/json": { "schema": { "$ref": "#/components/schemas/RegisterRequest" } }
                  }
                },
                "responses": {
                  "201": { "description": "Account created successfully" }
                }
              }
            },
            "/auth/me": {
              "get": {
                "summary": "Get authenticated user profile",
                "tags": ["Authentication"],
                "security": [{ "BearerAuth": [] }],
                "responses": {
                  "200": { "description": "User profile returned" }
                }
              }
            },
            "/cart": {
              "get": {
                "summary": "Get user shopping cart",
                "tags": ["Shopping Cart"],
                "security": [{ "BearerAuth": [] }],
                "responses": {
                  "200": { "description": "Shopping cart retrieved" }
                }
              }
            },
            "/orders": {
              "get": {
                "summary": "Get customer order history",
                "tags": ["Orders"],
                "security": [{ "BearerAuth": [] }],
                "responses": {
                  "200": { "description": "Orders list retrieved" }
                }
              },
              "post": {
                "summary": "Create new order from cart",
                "tags": ["Orders"],
                "security": [{ "BearerAuth": [] }],
                "responses": {
                  "201": { "description": "Order placed successfully" }
                }
              }
            },
            "/addresses": {
              "get": {
                "summary": "Get customer shipping addresses",
                "tags": ["Addresses"],
                "security": [{ "BearerAuth": [] }],
                "responses": {
                  "200": { "description": "Addresses list" }
                }
              }
            },
            "/payments/process": {
              "post": {
                "summary": "Process payment for order",
                "tags": ["Payments"],
                "security": [{ "BearerAuth": [] }],
                "responses": {
                  "200": { "description": "Payment processed" }
                }
              }
            }
          }
        }
        """;
        return Response.ok(jsonSpec, MediaType.APPLICATION_JSON_TYPE).build();
    }
}
