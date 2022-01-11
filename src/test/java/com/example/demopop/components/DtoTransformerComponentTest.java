package com.example.demopop.components;

import com.example.demopop.models.MailInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DtoTransformerComponentTest {

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    DtoTransformerComponent transformerComponent;

    @Test
    @SneakyThrows
    void dtoToFileShouldCreateAFileWithTheDtoContentAndReturnIt() {
        //given
        val givenDto = getDto();
        val expectedPath = Path.of(System.getProperty("java.io.tmpdir"), givenDto.getId() + ".json")
                .toAbsolutePath()
                .toString();
        doNothing().when(objectMapper).writeValue(any(File.class), eq(givenDto));

        //when
        val actualResult = transformerComponent.dtoToFile(givenDto);

        //then
        assertNotNull(actualResult);
        assertEquals(expectedPath, actualResult.getAbsolutePath());
    }

    @Test
    @SneakyThrows
    void dtoToFileShouldReturnNullOnError() {
        //given
        val givenDto = getDto();
        doThrow(RuntimeException.class).when(objectMapper).writeValue(any(File.class), eq(givenDto));

        //when
        val actualResult = transformerComponent.dtoToFile(givenDto);

        //then
        assertNull(actualResult);
    }

    @Test
    @SneakyThrows
    void dtoToJsonShouldConvertToJsonAndReturnIt() {
        //given
        val givenDto = getDto();
        val expectedJson = "my json";
        when(objectMapper.writeValueAsString(givenDto)).thenReturn(expectedJson);

        //when
        val actualResult = transformerComponent.dtoToJson(givenDto);

        //then
        assertNotNull(actualResult);
        assertEquals(expectedJson, actualResult);
    }

    @Test
    @SneakyThrows
    void dtoToJsonShouldReturnNullOnError() {
        //given
        val givenDto = getDto();
        when(objectMapper.writeValueAsString(givenDto)).thenThrow(RuntimeException.class);

        //when
        val actualResult = transformerComponent.dtoToJson(givenDto);

        //then
        assertNull(actualResult);
    }

    private static MailInfoDto getDto() {
        return MailInfoDto.builder()
                .id(UUID.randomUUID().toString())
                .build();
    }
}