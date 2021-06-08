package net.pedjango.learningdto.controller;

import net.pedjango.learningdto.dto.ContactDTO;
import net.pedjango.learningdto.dto.PersonDTO;
import net.pedjango.learningdto.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/person")
public class PersonController {
    private PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<PersonDTO> getAllPeople() {
        return personService.getAllPeople();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable("id") Long id) {
        return personService.getPersonById(id);
    }

    @PostMapping
    public ResponseEntity<Void> createPerson(@RequestBody PersonDTO personDTO) {
        return personService.createPerson(personDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePerson(@PathVariable("id") Long id, @RequestBody PersonDTO personDTO) {
        return personService.updatePerson(id, personDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonById(@PathVariable("id") Long id) {
        return personService.deletePersonById(id);
    }

    @GetMapping("/{id}/contacts")
    public ResponseEntity<List<ContactDTO>> getContactByPersonId(@PathVariable("id") Long id) {
        return personService.getContactByPersonId(id);
    }

    @PostMapping("/{id}/contacts")
    public ResponseEntity<Void> createContactByPersonId(@PathVariable("id") Long id, @RequestBody ContactDTO contactDTO) {
        return personService.createContactByPersonId(id, contactDTO);
    }

    @PutMapping("/{id}/contacts")
    public ResponseEntity<Void> updateContactByPersonId(@PathVariable("id") Long id, @RequestBody List<ContactDTO> contactDTO) {
        return personService.updateContactByPersonId(id, contactDTO);
    }
}
