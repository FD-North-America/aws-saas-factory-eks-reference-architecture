package com.amazonaws.saas.eks.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class DependencyViolationError extends ApiSubError {
    private String entity;
    private List<DependencyViolationErrorItem> items;
}
