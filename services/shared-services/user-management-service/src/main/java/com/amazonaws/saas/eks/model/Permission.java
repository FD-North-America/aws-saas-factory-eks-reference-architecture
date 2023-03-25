package com.amazonaws.saas.eks.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class Permission {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String label;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String group;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;

    public static class AttributeNames {
        public static final String NAME = "Name";
        public static final String LABEL = "Label";
        public static final String DESCRIPTION = "Description";
        public static final String GROUP = "Group";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
    }

    public static final String MANAGE_USERS_READ = "manage-users-read";
    public static final String MANAGE_USERS_CREATE = "manage-users-create";
    public static final String MANAGE_USERS_UPDATE = "manage-users-update";
    public static final String MANAGE_USERS_DELETE = "manage-users-delete";

    public static final String MANAGE_ROLES_READ = "manage-roles-read";
    public static final String MANAGE_ROLES_CREATE = "manage-roles-create";
    public static final String MANAGE_ROLES_UPDATE = "manage-roles-update";
    public static final String MANAGE_ROLES_DELETE = "manage-roles-delete";
}
