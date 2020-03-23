package com.bizzdesk.group.user.management.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EmailChannel {

    @Output(value = "gotax_emails")
    MessageChannel output();
}
