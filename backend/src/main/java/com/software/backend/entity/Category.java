package com.software.backend.entity;

public enum Category {
    FOOD,
    ELECTRONICS,
    CLOTHING,
    BOOKS,
    OTHER;

    public static boolean exists(String value) {
        for (Category c : Category.values()) {
            if (c.name().equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
