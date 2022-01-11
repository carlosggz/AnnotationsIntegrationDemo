package com.example.demopop.components;

import com.example.demopop.config.ConstantsUtils;
import com.example.demopop.models.MailInfoDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.annotation.Router;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ChannelRoutingComponent {
    private static final Map<String, String> ROUTES = Map.of(
            "^Info Required - \\[\\d{1,10}\\]$", ConstantsUtils.OUTBOUND_HTTP_CHANNEL,
            "^Confirmation - \\[\\d{1,10}\\]$", ConstantsUtils.CONFIRMATION_CHANNEL,
            "^Cancellation - \\[\\d{1,10}\\]$", ConstantsUtils.CANCELLATION_CHANNEL
    );

    @Router(inputChannel = ConstantsUtils.ROUTING_CHANNEL)
    public String getRoute(@NonNull MailInfoDto infoDto) {
        val subject = Optional.ofNullable(infoDto.getSubject()).orElse(StringUtils.EMPTY);

        val channel = ROUTES
                .entrySet()
                .stream()
                .filter(x -> Pattern.compile(x.getKey(), Pattern.CASE_INSENSITIVE).matcher(subject).find())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(ConstantsUtils.INVALID_CHANNEL);

        log.info("Routing to channel {}", channel);
        return channel;
    }
}
