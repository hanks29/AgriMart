package com.example.agrimart.data.model;

public class Province {
    private String _id;
    private String name;
    private String slug;
    private String type;
    private String name_with_type;
    private String code;
    private boolean isDeleted;

    // Getter và Setter cho các thuộc tính
    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
