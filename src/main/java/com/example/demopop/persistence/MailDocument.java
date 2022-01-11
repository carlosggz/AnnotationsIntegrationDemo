package com.example.demopop.persistence;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "mail_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "body")
public class MailDocument {
    @Id
    @Column(length = 36, columnDefinition = "varchar(36)", nullable = false)
    private String id;

    @Column(nullable = false)
    private String subject;

    @Column(length = 5000, columnDefinition = "varchar(5000)", nullable = false)
    private String body;

    @Column(length = 100, columnDefinition = "varchar(100)", nullable = false)
    private String fromAddress;

    @Column(nullable = false)
    private LocalDateTime creationDate;
}
