package com.buixuanphat.spot_on.enums;

public enum Role {
    ADMIN("admin"),
    STAFF("staff"),
    ORGANIZER("organizer"),
    CUSTOMER("customer");
    ;
    String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
