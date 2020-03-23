package com.bizzdesk.group.user.management.mapper;

import com.bizzdesk.group.user.management.entities.User;
import com.gotax.framework.library.entity.helpers.AccountCreationEmailHelper;

public class UserToAccountCreationEmailHelper {

    public static AccountCreationEmailHelper createEmailHelperFromUser(User user) {
        return new AccountCreationEmailHelper()
                .setEmailAddress(user.getEmailAddress())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setUserId(user.getUserId())
                .setVerificationCode(user.getVerificationCode());
    }
}
