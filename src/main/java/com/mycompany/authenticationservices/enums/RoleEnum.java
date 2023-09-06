package com.mycompany.authenticationservices.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum RoleEnum {
    ROLE_USER("ROLE_USER"),
    ROLE_GUEST("ROLE_GUEST"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MODERATOR("ROLE_MODERATOR"),
    ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN");
    private String code;
    private RoleEnum(String code) {
        this.code = code;
    }
    @JsonCreator
    public static RoleEnum decode(final String code) {
        return Stream.of(RoleEnum.values()).filter(targetEnum -> targetEnum.code.equals(code)).findFirst().orElse(null);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
