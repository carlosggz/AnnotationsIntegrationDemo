package com.example.demopop.components;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;

@ExtendWith(MockitoExtension.class)
class RemoveFileInterceptorComponentTest {

    @Mock
    MessageChannel channel;

    @Mock
    File file;

    static RemoveFileInterceptorComponent removeFileInterceptorComponent;

    @BeforeAll
    static void setup() {
        removeFileInterceptorComponent = new RemoveFileInterceptorComponent();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenPostSendAndTheFileExistsItIsDeleted(boolean sent) {
        //given
        val message = MessageBuilder.withPayload(file).build();
        Mockito.when(file.getAbsolutePath()).thenReturn("some-file-name");
        Mockito.when(file.exists()).thenReturn(true);
        Mockito.when(file.delete()).thenReturn(true);

        //when
        removeFileInterceptorComponent.postSend(message, channel, sent);

        //then
        Mockito.verify(file, Mockito.times(1)).getAbsolutePath();
        Mockito.verify(file, Mockito.times(1)).exists();
        Mockito.verify(file, Mockito.times(1)).delete();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenPostSendAndTheFileDoesNotExistsDeleteIsNotCalled(boolean sent) {
        //given
        val message = MessageBuilder.withPayload(file).build();
        Mockito.when(file.getAbsolutePath()).thenReturn("some-file-name");
        Mockito.when(file.exists()).thenReturn(false);

        //when
        removeFileInterceptorComponent.postSend(message, channel, sent);

        //then
        Mockito.verify(file, Mockito.times(1)).getAbsolutePath();
        Mockito.verify(file, Mockito.times(1)).exists();
    }
}