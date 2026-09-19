package com.onlinebookstore.author.bean;

import com.onlinebookstore.book.dto.AuthorRequest;
import com.onlinebookstore.book.dto.AuthorResponse;
import com.onlinebookstore.book.service.AuthorService;
import com.onlinebookstore.common.dto.ApiResponse;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminAuthorBean")
@ViewScoped
public class AdminAuthorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AuthorResponse> authors = new ArrayList<>();
    private String searchQuery;

    private AuthorRequest authorRequest = new AuthorRequest();
    private Integer selectedAuthorId;

    @Inject
    private AuthorService authorService;

    @PostConstruct
    public void init() {
        loadAuthors();
    }

    public void loadAuthors() {
        try {
            ApiResponse<List<AuthorResponse>> res = authorService.getAuthors(searchQuery);
            if (res != null && res.isSuccess() && res.getData() != null) {
                authors = res.getData();
            } else {
                authors = new ArrayList<>();
            }
        } catch (Exception e) {
            authors = new ArrayList<>();
        }
    }

    public void filterAuthors() {
        loadAuthors();
    }

    public void prepareAddAuthor() {
        selectedAuthorId = null;
        authorRequest = new AuthorRequest();
    }

    public void prepareEditAuthor(AuthorResponse a) {
        if (a == null) return;
        selectedAuthorId = a.getId();
        authorRequest = new AuthorRequest();
        authorRequest.setName(a.getName());
        authorRequest.setBio(a.getBio());
    }

    public void saveAuthor() {
        try {
            ApiResponse<AuthorResponse> res;
            if (selectedAuthorId == null) {
                res = authorService.createAuthor(authorRequest);
            } else {
                res = authorService.updateAuthor(selectedAuthorId, authorRequest);
            }

            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Author saved successfully!", null));
                loadAuthors();
                prepareAddAuthor();
            } else {
                String msg = res != null ? res.getMessage() : "Failed to save author.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void deleteAuthor(Integer id) {
        if (id == null) return;
        try {
            ApiResponse<String> res = authorService.deleteAuthor(id);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Author deleted successfully.", null));
                loadAuthors();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, res != null ? res.getMessage() : "Failed to delete author.", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // Getters and Setters
    public List<AuthorResponse> getAuthors() {
        return authors;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public AuthorRequest getAuthorRequest() {
        return authorRequest;
    }

    public void setAuthorRequest(AuthorRequest authorRequest) {
        this.authorRequest = authorRequest;
    }

    public Integer getSelectedAuthorId() {
        return selectedAuthorId;
    }

    public void setSelectedAuthorId(Integer selectedAuthorId) {
        this.selectedAuthorId = selectedAuthorId;
    }
}
