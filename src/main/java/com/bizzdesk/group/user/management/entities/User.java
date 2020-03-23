package com.bizzdesk.group.user.management.entities;

import lombok.*;
import lombok.experimental.Accessors;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "users")
public class User {

    @Id
    private String userId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @NotNull
    private String emailAddress;
    private String firstName;
    @OneToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role roleId;
    private String lastName;
    private String middleName;
    private String mobileNumber;
    private String password;
    private Long verificationCode;
    private boolean activeStatus;

}
