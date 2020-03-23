package com.bizzdesk.group.user.management.mapper;

import com.bizzdesk.group.user.management.entities.Role;
import com.gotax.framework.library.entity.helpers.RoleHelper;

public class RoleHelperToRole {

    public static Role mapRoleHelperToRole(RoleHelper roleHelper) {
        return new Role().setRoleName(roleHelper.getRoleName())
                .setRoleDescription(roleHelper.getRoleDescription());
    }
}
