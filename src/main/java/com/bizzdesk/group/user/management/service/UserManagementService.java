package com.bizzdesk.group.user.management.service;

import com.bizzdesk.group.user.management.entities.Role;
import com.bizzdesk.group.user.management.entities.User;
import com.bizzdesk.group.user.management.jwt.JwtUtils;
import com.bizzdesk.group.user.management.kafka.channel.EmailChannel;
import com.bizzdesk.group.user.management.kafka.channel.PasswordResetChannel;
import com.bizzdesk.group.user.management.mapper.RoleHelperToRole;
import com.bizzdesk.group.user.management.mapper.UserHelperToUser;
import com.bizzdesk.group.user.management.mapper.UserToAccountCreationEmailHelper;
import com.bizzdesk.group.user.management.repository.RoleRepository;
import com.bizzdesk.group.user.management.repository.UserRepository;
import com.gotax.framework.library.business.helper.BusinessHelper;
import com.gotax.framework.library.entity.helpers.*;
import com.gotax.framework.library.error.handling.GoTaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserManagementService  implements UserDetailsService {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private EmailChannel emailChannel;
    private PasswordResetChannel passwordResetChannel;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;

    @Value("${length.verification.code}")
    private int lengthOfVerificationCode;

    @Autowired
    public UserManagementService(RoleRepository roleRepository, UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailChannel emailChannel, PasswordResetChannel passwordResetChannel) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailChannel = emailChannel;
        this.passwordResetChannel = passwordResetChannel;
    }

    public void createRole(RoleHelper roleHelper) {
        Role role = RoleHelperToRole.mapRoleHelperToRole(roleHelper);
        roleRepository.save(role);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    private Optional<Role> findRoleById(String roleId) throws GoTaxException {
        return Optional.ofNullable(roleRepository.findById(roleId).orElseThrow(() -> new GoTaxException("Role Not Found In Database")));
    }

    public void createUser(UserHelper userHelper) throws GoTaxException {
        Optional<User> optionalUser = userRepository.findByEmailAddress(userHelper.getEmailAddress());
        if(optionalUser.isPresent()) {
            throw new GoTaxException("User with Email Address Already Exist");
        } else {
            Optional<Role> optionalRole = findRoleById(userHelper.getRoleId());
            if(optionalRole.isPresent()) {
                User user = UserHelperToUser.mapUserHelperToUser(userHelper, passwordEncoder)
                        .setCreatedDate(new Date())
                        .setActiveStatus(false)
                        .setRoleId(optionalRole.get())
                        .setVerificationCode(BusinessHelper.generateSixDigitNumber(lengthOfVerificationCode))
                        .setUserId(UUID.randomUUID().toString());
                userRepository.save(user);
                AccountCreationEmailHelper accountCreationEmailHelper = UserToAccountCreationEmailHelper.createEmailHelperFromUser(user);
                emailChannel.output().send(MessageBuilder.withPayload(accountCreationEmailHelper).build());
            }
        }
    }

    public void activateAccount(String userId, String verificationCode) throws GoTaxException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserIdAndVerificationCode(userId, Long.valueOf(verificationCode)).orElseThrow(
                () -> new GoTaxException("Account Could Not Be Verified. Contact Administrator")
        ));
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.isActiveStatus()) {
                throw new GoTaxException("Account Has Already Been Activated");
            } else {
                user.setActiveStatus(true);
                userRepository.save(user);
            }
        }
    }

    public void deActivateAccount(String userId) throws GoTaxException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findById(userId).orElseThrow(
                () -> new GoTaxException("No User with ID Found")
        ));
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.isActiveStatus()) {
                user.setActiveStatus(false);
                userRepository.save(user);
            } else {
                throw new GoTaxException("Account Has Not Been Activated");
            }
        }
    }

    public void resetPassword(String userId) throws GoTaxException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findById(userId).orElseThrow(
                () -> new GoTaxException("No User with ID Found")
        ));
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.isActiveStatus()) {
                user.setActiveStatus(false);
                userRepository.save(user);
                AccountCreationEmailHelper accountCreationEmailHelper = UserToAccountCreationEmailHelper.createEmailHelperFromUser(user);
                passwordResetChannel.output().send(MessageBuilder.withPayload(accountCreationEmailHelper).build());
            } else {
                throw new GoTaxException("Account Has Not Been Activated");
            }
        }
    }
    public void updatePassword(PasswordResetHelper passwordResetHelper) throws GoTaxException {
        String userId = passwordResetHelper.getUserId();
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findById(userId).orElseThrow(
                () -> new GoTaxException("No User with ID Found")
        ));
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(!user.isActiveStatus()) {
                String password = passwordResetHelper.getPassword();
                user.setPassword(passwordEncoder.encode(password))
                        .setActiveStatus(true);
                userRepository.save(user);
            } else {
                throw new GoTaxException("Account Password Reset Has Not Been Initiated. Contact Administrator");
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + emailAddress));

        return UserDetailsImpl.build(user);
    }

    public LoginResponseHelper authenticateUser(LoginHelper loginHelper) throws GoTaxException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginHelper.getUserId(), loginHelper.getPassword()));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = "";
        if(userDetails.isActiveStatus()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = jwtUtils.generateJwtToken(authentication);
        } else {
            throw new GoTaxException("Account Has Not Been Activated. Contact Administrator");
        }

        return new LoginResponseHelper(jwt);
    }

}
