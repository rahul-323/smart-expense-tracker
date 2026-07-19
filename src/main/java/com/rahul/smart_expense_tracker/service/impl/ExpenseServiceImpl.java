package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.request.ExpenseRequest;
import com.rahul.smart_expense_tracker.dto.response.ExpenseResponse;
import com.rahul.smart_expense_tracker.dto.response.PagedResponse;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.Expense;
import com.rahul.smart_expense_tracker.entity.Tag;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.ExpenseStatus;
import com.rahul.smart_expense_tracker.enums.PaymentMethod;
import com.rahul.smart_expense_tracker.exception.BadRequestException;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;
import com.rahul.smart_expense_tracker.mapper.ExpenseMapper;
import com.rahul.smart_expense_tracker.repository.CategoryRepository;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.repository.TagRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.service.ExpenseService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseMapper expenseMapper;

    private User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","email",email));
    }

    private Category getCategoryForUser(Long categoryId,Long userId){
        return categoryRepository.findCategoryByIdForUser(categoryId,userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null || tagNames.isEmpty()) return tags;

        for (String name : tagNames) {
            String trimmed = name.trim().toLowerCase();
            if (trimmed.isEmpty()) continue;

            Tag tag = tagRepository.findByNameIgnoreCase(trimmed)
                    .orElseGet(() -> tagRepository.save(
                            Tag.builder().name(trimmed).build()
                    ));
            tags.add(tag);
        }
        return tags;
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    // ─── CREATE ───
    @Override
    public ExpenseResponse createExpense(ExpenseRequest request, String email) {
        User user = getUserByEmail(email);
        Category category = getCategoryForUser(request.getCategoryId(), user.getUserId());
        Set<Tag> tags = resolveTags(request.getTagNames());

        Expense expense = expenseMapper.toEntity(request, user, category, tags);
        Expense saved = expenseRepository.save(expense);
        return expenseMapper.toResponse(saved);
    }

    // ─── GET ALL (paginated) ───
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> getAllExpenses(
            String email, int page, int size, String sortBy, String sortDir
    ) {
        User user = getUserByEmail(email);
        Pageable pageable = buildPageable(page, size,
                sortBy != null ? sortBy : "expenseDate",
                sortDir != null ? sortDir : "desc");

        Page<Expense> expensePage = expenseRepository.findByUserUserId(user.getUserId(), pageable);
        List<ExpenseResponse> content = expenseMapper.toResponseList(expensePage.getContent());
        return PagedResponse.from(expensePage, content);
    }

    // ─── GET BY ID ───
    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long expenseId, String email) {
        User user = getUserByEmail(email);
        Expense expense = expenseRepository
                .findByExpenseIdAndUserUserId(expenseId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        return expenseMapper.toResponse(expense);
    }


    // ─── UPDATE ───
    @Override
    public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request, String email) {
        User user = getUserByEmail(email);
        Expense expense = expenseRepository
                .findByExpenseIdAndUserUserId(expenseId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        Category category = getCategoryForUser(request.getCategoryId(), user.getUserId());
        Set<Tag> tags = resolveTags(request.getTagNames());

        expenseMapper.updateEntity(expense, request, category, tags);
        Expense updated = expenseRepository.save(expense);
        return expenseMapper.toResponse(updated);
    }

    // ─── DELETE ───
    @Override
    public void deleteExpense(Long expenseId, String email) {
        User user = getUserByEmail(email);
        Expense expense = expenseRepository
                .findByExpenseIdAndUserUserId(expenseId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        expenseRepository.delete(expense);
    }

    // ─── FILTER (Dynamic — Multiple Criteria) ⭐ ───
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> filterExpenses(
            String email,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            PaymentMethod paymentMethod,
            ExpenseStatus status,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        User user = getUserByEmail(email);

        // Build dynamic Specification
        Specification<Expense> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by user
            predicates.add(cb.equal(root.get("user").get("userId"), user.getUserId()));

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), endDate));
            }
            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            if (paymentMethod != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), paymentMethod));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (tags != null && !tags.isEmpty()) {
                Join<Expense, Tag> tagJoin = root.join("tags");
                List<String> lowerTags = tags.stream()
                        .map(t -> t.trim().toLowerCase())
                        .toList();
                predicates.add(tagJoin.get("name").in(lowerTags));
                query.distinct(true);  // Prevent duplicates from join
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = buildPageable(page, size,
                sortBy != null ? sortBy : "expenseDate",
                sortDir != null ? sortDir : "desc");

        Page<Expense> expensePage = expenseRepository.findAll(spec, pageable);
        List<ExpenseResponse> content = expenseMapper.toResponseList(expensePage.getContent());
        return PagedResponse.from(expensePage, content);
    }

    // ─── SEARCH BY KEYWORD ───
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> searchExpenses(
            String email, String keyword, int page, int size
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("Search keyword cannot be empty");
        }

        User user = getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseDate").descending());

        Page<Expense> expensePage = expenseRepository
                .findByUserUserIdAndDescriptionContainingIgnoreCase(
                        user.getUserId(), keyword.trim(), pageable
                );

        List<ExpenseResponse> content = expenseMapper.toResponseList(expensePage.getContent());
        return PagedResponse.from(expensePage, content);
    }
}
