package com.example.librarymanager.constant;

public enum RoleConstant {
    ROLE_MANAGE_AUTHOR("Quản lý tác giả"),
    ROLE_MANAGE_BOOK("Quản lý sách"),
    ROLE_MANAGE_BOOK_DEFINITION("Quản lý biên mục"),
    ROLE_MANAGE_BOOK_SET("Quản lý bộ sách"),
    ROLE_MANAGE_CATEGORY("Quản lý danh mục"),
    ROLE_MANAGE_CATEGORY_GROUP("Quản lý nhóm danh mục"),
    ROLE_MANAGE_CLASSIFICATION_SYMBOL("Quản lý ký hiệu phân loại"),
    ROLE_MANAGE_IMPORT_RECEIPT("Quản lý phiếu nhập"),
    ROLE_MANAGE_EXPORT_RECEIPT("Quản lý phiếu xuất"),
    ROLE_MANAGE_LOG("Quản lý nhật ký"),
    ROLE_MANAGE_NEWS_ARTICLE("Quản lý bài viết tin tức"),
    ROLE_MANAGE_PUBLISHER("Quản lý nhà xuất bản"),
    ROLE_MANAGE_USER("Quản lý người dùng"),
    ROLE_MANAGE_USER_GROUP("Quản lý nhóm người dùng"),
    ROLE_MANAGE_SYSTEM_SETTINGS("Quản lý cài đặt hệ thống"),
    ROLE_MANAGE_READER("Quản lý bạn đọc"),
    ROLE_MANAGE_BORROW_RECEIPT("Quản lý phiếu mượn"),
    ROLE_READER("Độc giả");

    private final String roleName;

    RoleConstant(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
