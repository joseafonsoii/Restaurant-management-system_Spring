package ao.jose.restaurant_management_system.dto;

public class CategoryOrderCountDTO {
    private String categoryName;
    private Long orderCount;

    public CategoryOrderCountDTO() {}

    public CategoryOrderCountDTO(String categoryName, Long orderCount) {
        this.categoryName = categoryName;
        this.orderCount = orderCount;
    }

    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Long getOrderCount() { return orderCount; }
    public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
}