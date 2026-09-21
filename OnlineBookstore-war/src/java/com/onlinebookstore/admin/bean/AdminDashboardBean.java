package com.onlinebookstore.admin.bean;

import com.onlinebookstore.book.dto.BookResponse;
import com.onlinebookstore.book.dto.CategoryResponse;
import com.onlinebookstore.book.service.AuthorService;
import com.onlinebookstore.book.service.BookService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.service.IOrderService;
import com.onlinebookstore.user.dto.UserResponse;
import com.onlinebookstore.user.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("adminDashboardBean")
@ViewScoped
public class AdminDashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalBooks = 0;
    private int totalUsers = 0;
    private int totalOrders = 0;
    private int totalAuthors = 0;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private int pendingOrdersCount = 0;
    private int completedOrdersCount = 0;

    private List<OrderResponse> recentOrders = new ArrayList<>();

    // Chart Data as JSON strings
    private String statusChartLabelsJson = "[]";
    private String statusChartDataJson = "[]";

    private String revenueChartLabelsJson = "[]";
    private String revenueChartDataJson = "[]";

    private String categoryChartLabelsJson = "[]";
    private String categoryChartDataJson = "[]";

    @Inject
    private BookService bookService;

    @Inject
    private AuthorService authorService;

    @Inject
    private UserService userService;

    @Inject
    private IOrderService orderService;

    @PostConstruct
    public void init() {
        loadMetrics();
    }

    public void loadMetrics() {
        // Load Books & Categories
        List<BookResponse> booksList = new ArrayList<>();
        try {
            ApiResponse<List<BookResponse>> booksRes = bookService.getBooks(null, null, null, null);
            if (booksRes != null && booksRes.isSuccess() && booksRes.getData() != null) {
                booksList = booksRes.getData();
                totalBooks = booksList.size();
            }
        } catch (Exception ignored) {}

        // Load Categories
        List<CategoryResponse> categoriesList = new ArrayList<>();
        try {
            ApiResponse<List<CategoryResponse>> catRes = bookService.getCategories();
            if (catRes != null && catRes.isSuccess() && catRes.getData() != null) {
                categoriesList = catRes.getData();
            }
        } catch (Exception ignored) {}

        // Load Users
        try {
            ApiResponse<List<UserResponse>> usersRes = userService.getUsers(null, "ALL");
            if (usersRes != null && usersRes.isSuccess() && usersRes.getData() != null) {
                totalUsers = usersRes.getData().size();
            }
        } catch (Exception ignored) {}

        // Load Authors
        try {
            ApiResponse<List<com.onlinebookstore.book.dto.AuthorResponse>> authorRes = authorService.getAuthors(null);
            if (authorRes != null && authorRes.isSuccess() && authorRes.getData() != null) {
                totalAuthors = authorRes.getData().size();
            }
        } catch (Exception ignored) {}

        // Load Orders & Revenue Calculations
        List<OrderResponse> ordersList = new ArrayList<>();
        try {
            ApiResponse<List<OrderResponse>> ordersRes = orderService.getAllOrders(null);
            if (ordersRes != null && ordersRes.isSuccess() && ordersRes.getData() != null) {
                ordersList = ordersRes.getData();
                totalOrders = ordersList.size();
            }
        } catch (Exception ignored) {}

        int pendingCount = 0;
        int completedCount = 0;
        int processingCount = 0;
        int shippedCount = 0;
        int cancelledCount = 0;

        BigDecimal revDelivered = BigDecimal.ZERO;
        BigDecimal revShipped = BigDecimal.ZERO;
        BigDecimal revProcessing = BigDecimal.ZERO;
        BigDecimal revPending = BigDecimal.ZERO;

        BigDecimal totalRev = BigDecimal.ZERO;

        for (OrderResponse o : ordersList) {
            String st = o.getStatus() != null ? o.getStatus().toUpperCase() : "PENDING";
            BigDecimal amt = o.getFinalAmount() != null ? o.getFinalAmount() : BigDecimal.ZERO;

            switch (st) {
                case "PENDING":
                    pendingCount++;
                    revPending = revPending.add(amt);
                    break;
                case "PROCESSING":
                    processingCount++;
                    revProcessing = revProcessing.add(amt);
                    break;
                case "SHIPPED":
                    shippedCount++;
                    revShipped = revShipped.add(amt);
                    break;
                case "DELIVERED":
                case "COMPLETED":
                    completedCount++;
                    revDelivered = revDelivered.add(amt);
                    totalRev = totalRev.add(amt);
                    break;
                case "CANCELLED":
                    cancelledCount++;
                    break;
                default:
                    pendingCount++;
                    break;
            }
        }

        this.pendingOrdersCount = pendingCount + processingCount;
        this.completedOrdersCount = completedCount;
        this.totalRevenue = totalRev;

        // Recent Orders (Top 5)
        this.recentOrders = ordersList.stream()
                .limit(5)
                .collect(Collectors.toList());

        // Status Chart Data
        this.statusChartLabelsJson = "[\"Pending\", \"Processing\", \"Shipped\", \"Delivered\", \"Cancelled\"]";
        this.statusChartDataJson = String.format("[%d, %d, %d, %d, %d]",
                pendingCount, processingCount, shippedCount, completedCount, cancelledCount);

        // Revenue Breakdown Chart Data
        this.revenueChartLabelsJson = "[\"Delivered\", \"Shipped\", \"Processing\", \"Pending\"]";
        this.revenueChartDataJson = String.format("[%.2f, %.2f, %.2f, %.2f]",
                revDelivered.doubleValue(), revShipped.doubleValue(), revProcessing.doubleValue(), revPending.doubleValue());

        // Category Book Count Chart Data
        Map<String, Integer> catCounts = new HashMap<>();
        for (CategoryResponse cat : categoriesList) {
            catCounts.put(cat.getName(), 0);
        }
        for (BookResponse b : booksList) {
            if (b.getCategoryName() != null) {
                catCounts.put(b.getCategoryName(), catCounts.getOrDefault(b.getCategoryName(), 0) + 1);
            }
        }

        List<String> catLabels = new ArrayList<>();
        List<Integer> catData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : catCounts.entrySet()) {
            catLabels.add("\"" + entry.getKey().replace("\"", "\\\"") + "\"");
            catData.add(entry.getValue());
        }

        this.categoryChartLabelsJson = catLabels.toString();
        this.categoryChartDataJson = catData.toString();
    }

    // Getters
    public int getTotalBooks() {
        return totalBooks;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public int getTotalAuthors() {
        return totalAuthors;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public int getPendingOrdersCount() {
        return pendingOrdersCount;
    }

    public int getCompletedOrdersCount() {
        return completedOrdersCount;
    }

    public List<OrderResponse> getRecentOrders() {
        return recentOrders;
    }

    public String getStatusChartLabelsJson() {
        return statusChartLabelsJson;
    }

    public String getStatusChartDataJson() {
        return statusChartDataJson;
    }

    public String getRevenueChartLabelsJson() {
        return revenueChartLabelsJson;
    }

    public String getRevenueChartDataJson() {
        return revenueChartDataJson;
    }

    public String getCategoryChartLabelsJson() {
        return categoryChartLabelsJson;
    }

    public String getCategoryChartDataJson() {
        return categoryChartDataJson;
    }
}
