package com.amazonaws.saas.eks.dto.responses.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String level;

    @Getter
    @Setter
    private String mainCategoryId;

    @Getter
    @Setter
    private String subCategoryId;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;

    @Getter
    @Setter
    private CategoryResponse[] categories;

    @JsonIgnore
    public int getCount() {
        int count = 1;
        if (categories != null && categories.length > 0) {
            for (CategoryResponse cr: categories) {
                count += cr.getCount();
            }
        }
        return count;
    }
}
