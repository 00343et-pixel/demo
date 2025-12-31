package com.example.demo.practice.dto.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "page")
public class PageResponse<T> implements Serializable {

    @Schema(description = "page content")
    private List<T> content;

    @Schema(description = "number of page", example = "0")
    private int pageNumber;

    @Schema(description = "size of page", example = "10")
    private int pageSize;
    
    @Schema(description = "number of total elements")
    private long totalElements;
}
