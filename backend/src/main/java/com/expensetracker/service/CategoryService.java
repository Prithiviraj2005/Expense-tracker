package com.expensetracker.service;

import com.expensetracker.dto.CategoryRequest;
import com.expensetracker.dto.CategoryResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Create category for the currently logged-in user
    public CategoryResponse createCategory(CategoryRequest request) {

        User user = getCurrentUser();

        if (categoryRepository.existsByUserIdAndNameIgnoreCase(
                user.getId(),
                request.getName())) {

            throw new IllegalArgumentException(
                    "Category already exists: " + request.getName());
        }

        Category category = new Category(
                request.getName(),
                request.getType()
        );

        // Automatically assign logged-in user
        category.setUser(user);

        category = categoryRepository.save(category);

        return mapToResponse(category);
    }

    // Get only categories belonging to logged-in user
    public List<CategoryResponse> getAllCategories() {

        User user = getCurrentUser();

        return categoryRepository
                .findByUserIdOrderByNameAsc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get only logged-in user's categories of a particular type
    public List<CategoryResponse> getCategoriesByType(
            TransactionType type) {

        User user = getCurrentUser();

        return categoryRepository
                .findByUserIdAndType(user.getId(), type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update only the logged-in user's category
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        User user = getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id));

        // Security check
        if (category.getUser() == null ||
                !category.getUser().getId().equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Category not found with id: " + id);
        }

        category.setName(request.getName());
        category.setType(request.getType());

        category = categoryRepository.save(category);

        return mapToResponse(category);
    }

    // Delete only the logged-in user's category
    public void deleteCategory(Long id) {

        User user = getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id));

        // Security check
        if (category.getUser() == null ||
                !category.getUser().getId().equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Category not found with id: " + id);
        }

        categoryRepository.delete(category);
    }

    // Get currently logged-in user
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"));
    }

    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getCreatedAt()
        );
    }
}