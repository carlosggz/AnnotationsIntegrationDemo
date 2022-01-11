package com.example.demopop.components;

import com.example.demopop.config.ConstantsUtils;
import com.example.demopop.models.MailInfoDto;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChannelRoutingComponentTest {
    private static ChannelRoutingComponent routingComponent;

    @BeforeAll
    static void setup() {
        routingComponent = new ChannelRoutingComponent();
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String subject, String expectedResult) {
        //given
        val dto = MailInfoDto.builder()
                .subject(subject)
                .build();

        //when
        val actualResult = routingComponent.getRoute(dto);

        //then
        assertEquals(expectedResult, actualResult);
    }

    static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of("", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("   ", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("some subject", ConstantsUtils.INVALID_CHANNEL),

                Arguments.of("More Info Required - [112]", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Info Required - [112] other text", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Info Required  - [112]", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Info Required - [112]", ConstantsUtils.OUTBOUND_HTTP_CHANNEL),
                Arguments.of("info required - [112]", ConstantsUtils.OUTBOUND_HTTP_CHANNEL),
                Arguments.of("INFO REQUIRED - [112]", ConstantsUtils.OUTBOUND_HTTP_CHANNEL),

                Arguments.of("Ok Confirmation - [112]", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Confirmation - [112] other text", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Confirmation  - [112]", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Confirmation - [112]", ConstantsUtils.CONFIRMATION_CHANNEL),
                Arguments.of("confirmation - [112]", ConstantsUtils.CONFIRMATION_CHANNEL),
                Arguments.of("CONFIRMATION - [112]", ConstantsUtils.CONFIRMATION_CHANNEL),

                Arguments.of("KO Cancellation - [112]", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Cancellation - [112] other text", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Cancellation  - [112]", ConstantsUtils.INVALID_CHANNEL),
                Arguments.of("Cancellation - [112]", ConstantsUtils.CANCELLATION_CHANNEL),
                Arguments.of("cancellation - [112]", ConstantsUtils.CANCELLATION_CHANNEL),
                Arguments.of("CANCELLATION - [112]", ConstantsUtils.CANCELLATION_CHANNEL)
        );
    }

}