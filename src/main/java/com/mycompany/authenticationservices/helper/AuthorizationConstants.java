package com.mycompany.authenticationservices.helper;

public interface AuthorizationConstants {




    String PERMISSION_PREFIX="'";
    String PERMISSION_POSTFIX="'";
    String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN , ";
    String ROLE_ADMIN="ROLE_ADMIN";
    //UserController

    String READ_USERS =PERMISSION_PREFIX+ ROLE_SUPER_ADMIN + "read-users" + PERMISSION_POSTFIX;
    String READ_USER =PERMISSION_PREFIX+ ROLE_SUPER_ADMIN + "read-user"+ PERMISSION_POSTFIX;
    String CREATE_USER =PERMISSION_PREFIX+ ROLE_SUPER_ADMIN + "create-user"+ PERMISSION_POSTFIX;
    String UPDATE_USER =PERMISSION_PREFIX+ ROLE_SUPER_ADMIN + "update-user"+ PERMISSION_POSTFIX;
    String SEARCH_USER =PERMISSION_PREFIX+ ROLE_SUPER_ADMIN + "search-users"+ PERMISSION_POSTFIX;

}
