package com.rahul.smart_expense_tracker.repository;

import com.rahul.smart_expense_tracker.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository  extends JpaRepository<Tag,Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    Boolean existsByNameIgnoreCase(String name);
}
