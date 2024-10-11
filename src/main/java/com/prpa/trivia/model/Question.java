package com.prpa.trivia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinTable(name = "Question_Category",
            uniqueConstraints = @UniqueConstraint(name = "question_category_unique", columnNames = {"question_id", "category_id"}),
            joinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false))
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Category> category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @NotBlank
    @Length(min = 5)
    @Column(name = "statement", nullable = false, unique = true, columnDefinition = "TEXT")
    private String statement;

    @NotEmpty
    @Column(name = "alternatives", nullable = false)
    private List<String> alternatives;

    @NotNull
    @Column(name = "correct", nullable = false)
    private Integer correctIndex;
}
