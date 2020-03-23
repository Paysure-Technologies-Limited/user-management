package com.bizzdesk.group.user.management.mapper;

import com.bizzdesk.group.user.management.entities.User;
import com.gotax.framework.library.entity.helpers.UserHelper;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserHelperToUser {

    public static User mapUserHelperToUser(UserHelper userHelper, PasswordEncoder passwordEncoder) {
        return new User().setEmailAddress(userHelper.getEmailAddress())
                .setFirstName(userHelper.getFirstName())
                .setLastName(userHelper.getLastName())
                .setMiddleName(userHelper.getMiddleName())
                .setMobileNumber(userHelper.getMobileNumber())
                .setPassword(passwordEncoder.encode(userHelper.getPassword()));
    }
}
