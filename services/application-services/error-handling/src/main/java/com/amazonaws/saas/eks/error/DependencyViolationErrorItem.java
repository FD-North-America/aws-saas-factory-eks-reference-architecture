package com.amazonaws.saas.eks.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class DependencyViolationErrorItem {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;
}
