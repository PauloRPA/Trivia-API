package com.prpa.trivia.model.dto;

import com.prpa.trivia.model.Difficulty;
import com.prpa.trivia.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {

    @NotBlank(message = "error.question.empty.statement.message")
    private String statement;

    @NotNull(message = "error.question.empty.difficulty.message")
    private Difficulty difficulty;

    @NotNull(message = "error.question.empty.type.message")
    private Type type;

    @NotEmpty(message = "error.question.empty.alternatives.message")
    private List<String> alternatives;

    @NotNull(message = "error.question.empty.correctIndex.message")
    @PositiveOrZero(message = "error.question.positive.correctIndex.message")
    private Integer correctIndex;

    @NotEmpty(message = "error.question.empty.category.message")
    private List<CategoryDTO> category;
}
