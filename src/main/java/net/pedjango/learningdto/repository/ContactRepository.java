package net.pedjango.learningdto.repository;

import net.pedjango.learningdto.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByEmail(String email);

    Optional<Contact> findByPhone(String phone);
}
