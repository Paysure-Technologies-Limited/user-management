package com.bizzdesk.group.user.management.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface VerificationEmailChannel {

    @Output(value = "gotax-email-verification")
    MessageChannel output();
}
