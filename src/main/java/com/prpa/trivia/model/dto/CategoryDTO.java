package com.prpa.trivia.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class CategoryDTO {

    @NotBlank(message = "error.category.empty.name.message")
    private String name;

}
