package com.example.agrimart.data.model;

public class Ward {
    private String _id;
    private String name;
    private String type;
    private String slug;
    private String name_with_type;
    private String code;
    private String parent_code;
    private boolean isDeleted;

    public String getId() {
        return _id;
    }
    public String getName() { return name; }

    public String getParentCode() {
        return parent_code; // Getter cho parent_code
    }
}
