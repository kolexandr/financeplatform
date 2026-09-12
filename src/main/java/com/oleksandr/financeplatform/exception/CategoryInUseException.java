package com.oleksandr.financeplatform.exception;

public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException() {
        super("Category cannot be deleted while it has transactions or budgets");
    }
}
