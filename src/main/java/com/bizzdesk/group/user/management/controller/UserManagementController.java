package com.bizzdesk.group.user.management.controller;

import com.bizzdesk.group.user.management.entities.Role;
import com.bizzdesk.group.user.management.service.UserManagementService;
import com.gotax.framework.library.entity.helpers.*;
import com.gotax.framework.library.error.handling.Error;
import com.gotax.framework.library.error.handling.GoTaxException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserManagementController {

    private UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping(value = "/v1/auth/roles/list")
    public List<Role> listRoles() {
        return userManagementService.listRoles();
    }

    @PostMapping(value = "/v1/auth/role/create")
    public void createRole(@RequestBody RoleHelper roleHelper) {
        userManagementService.createRole(roleHelper);
    }

    @PostMapping(value = "/v1/user/create")
    @ApiOperation(value = "createUser", notes = "Create User")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public void createUser(@RequestBody UserHelper userHelper) throws GoTaxException {
        userManagementService.createUser(userHelper);
    }

    @PostMapping(value = "/v1/user/activate")
    public void activateAccount(@RequestParam(value = "userId") String userId,
                                @RequestParam(value = "verificationCode") String verificationCode) throws GoTaxException {
        userManagementService.activateAccount(userId, verificationCode);
    }

    @PostMapping("/v1/user/signin")
    public LoginResponseHelper authenticateUser(@Valid @RequestBody LoginHelper loginHelper) {
        return userManagementService.authenticateUser(loginHelper);
    }

    @PostMapping("/v1/auth/user/updatepassword")
    public void updatePassword(@Valid @RequestBody PasswordResetHelper passwordResetHelper) throws GoTaxException {
        userManagementService.updatePassword(passwordResetHelper);
    }

    @PostMapping("/v1/auth/user/resetpassword")
    public void resetPassword(@RequestParam("userId") String userId) throws GoTaxException {
        userManagementService.resetPassword(userId);
    }

    @PostMapping("/v1/auth/user/deactivate")
    public void deactivateAccount(@RequestParam("userId") String userId) throws GoTaxException {
        userManagementService.deActivateAccount(userId);
    }
}
