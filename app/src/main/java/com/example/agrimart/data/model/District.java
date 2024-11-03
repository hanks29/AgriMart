package com.example.agrimart.data.model;

public class District {
    private String _id;
    private String name;
    private String type;
    private String slug;
    private String name_with_type;
    private String path;
    private String path_with_type;
    private String code;
    private String parent_code; // Thêm thuộc tính này
    private boolean isDeleted;

    // Getters
    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getParentCode() {
        return parent_code; // Getter cho parent_code
    }
}

