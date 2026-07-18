package com.rahul.smart_expense_tracker.entity;

//(no FK dependencies)

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tags")
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false,unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags",fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Expense> expenses=new HashSet<>();



}
