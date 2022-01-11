package com.example.demopop.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MailInfoDto {
    private String id;
    private String subject;
    private String body;
    private String fromAddress;
}
