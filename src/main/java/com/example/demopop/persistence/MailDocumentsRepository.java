package com.example.demopop.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MailDocumentsRepository extends JpaRepository<MailDocument, String> {
}
