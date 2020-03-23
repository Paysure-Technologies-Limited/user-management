package com.bizzdesk.group.user.management.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PasswordResetChannel {

    @Output(value = "password_reset_emails")
    MessageChannel output();
}
