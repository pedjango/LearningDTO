package net.pedjango.learningdto.service;

import net.pedjango.learningdto.dto.ContactDTO;
import net.pedjango.learningdto.dto.PersonDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PersonService {
    List<PersonDTO> getAllPeople();
    ResponseEntity<PersonDTO> getPersonById(Long id);
    ResponseEntity<Void> createPerson(PersonDTO personDTO);
    ResponseEntity<Void> updatePerson(Long id, PersonDTO personDTO);
    ResponseEntity<Void> deletePersonById(Long id);

    /*
     * Methods regarding the contact information
     * of a given person, such as getting, adding
     * and/or updating contact information.
     * */
    ResponseEntity<List<ContactDTO>> getContactByPersonId(Long id);
    ResponseEntity<Void> createContactByPersonId(Long id, ContactDTO contactDTO);
    ResponseEntity<Void> updateContactByPersonId(Long id, List<ContactDTO> contacts);
}
