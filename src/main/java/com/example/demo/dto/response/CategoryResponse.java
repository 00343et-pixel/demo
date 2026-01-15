package com.example.demo.dto.response;

import java.io.Serializable;

import com.example.demo.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "category imformation")
public class CategoryResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Schema(description = "category id", example = "1")
    private Long id;

    @Schema(description = "category name", example = "electronics")
    private String name;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName()
        );
    }
}
