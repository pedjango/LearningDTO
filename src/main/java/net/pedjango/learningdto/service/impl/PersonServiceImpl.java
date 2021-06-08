package net.pedjango.learningdto.service.impl;

import net.pedjango.learningdto.dto.ContactDTO;
import net.pedjango.learningdto.dto.PersonDTO;
import net.pedjango.learningdto.model.Contact;
import net.pedjango.learningdto.model.Person;
import net.pedjango.learningdto.repository.ContactRepository;
import net.pedjango.learningdto.repository.PersonRepository;
import net.pedjango.learningdto.service.PersonService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PersonServiceImpl implements PersonService {
    private ModelMapper modelMapper;
    private PersonRepository personRepository;
    private ContactRepository contactRepository;

    @Autowired
    public PersonServiceImpl(ModelMapper modelMapper, PersonRepository personRepository, ContactRepository contactRepository) {
        this.modelMapper = modelMapper;
        this.personRepository = personRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public List<PersonDTO> getAllPeople() {
        List<Person> persons = personRepository.findAll();
        List<PersonDTO> personDTOList = new ArrayList<>();

        persons
            .stream()
            .forEach(p -> personDTOList.add(modelMapper.map(p, PersonDTO.class)));

        return personDTOList;
    }

    @Override
    public ResponseEntity<PersonDTO> getPersonById(Long id) {
        Optional<Person> optional = personRepository.findById(id);
        PersonDTO personDTO = optional.map(person -> modelMapper.map(person, PersonDTO.class)).orElse(null);

        if (personDTO != null) {
            return ResponseEntity
                    .ok(personDTO);
        } else {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @Override
    public ResponseEntity<Void> createPerson(PersonDTO personDTO) {
        List<ContactDTO> insertedContacts = personDTO.getContacts();
        AtomicReference<Boolean> isPresent = new AtomicReference<>(false);

        insertedContacts.stream().forEach(
            x -> {
                Optional<Contact> optionalEmail = contactRepository.findByEmail(x.getEmail());
                Optional<Contact> optionalPhone = contactRepository.findByPhone(x.getPhone());
                Contact email = optionalEmail.orElse(null);
                Contact phone = optionalPhone.orElse(null);
                if (email != null || phone != null) {
                    isPresent.set(true);
                }
            }
        );

        if (isPresent.get()) {
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .build();
        } else {
            Person person = modelMapper.map(personDTO, Person.class);
            this.personRepository.save(person);

            List<ContactDTO> contacts = personDTO.getContacts();
            contacts.stream().forEach(
                    c -> {
                        Contact contact = modelMapper.map(c, Contact.class);
                        contact.setPersonId(person.getId());
                        this.contactRepository.save(contact);
                    }
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", "/person/" + person.getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .headers(responseHeaders)
                    .build();
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Void> updatePerson(Long id, PersonDTO personDTO) {
        Optional<Person> optional = personRepository.findById(id);
        Person person = optional.orElse(null);

        if (person != null) {
            person.setFirstName(personDTO.getFirstName());
            person.setLastName(personDTO.getLastName());
            personRepository.save(person);

            HttpHeaders response = new HttpHeaders();
            response.set("Content-Location", "Person updated @ID " + id);

            return ResponseEntity
                    .noContent()
                    .headers(response)
                    .build();
        } else {
            HttpHeaders response = new HttpHeaders();
            response.set("Content-Location", "No person @ID " + id);

            return ResponseEntity
                    .notFound()
                    .headers(response)
                    .build();
        }
    }

    @Override
    public ResponseEntity<Void> deletePersonById(Long id) {
        Optional<Person> optional = personRepository.findById(id);
        Person person = optional.orElse(null);
        if (person != null) {
            contactRepository.deleteAllInBatch(person.getContacts());
            personRepository.deleteById(id);

            HttpHeaders response = new HttpHeaders();
            response.set("Content-Location", "Deleted person @ID " + id);

            return ResponseEntity
                    .accepted()
                    .headers(response)
                    .build();
        } else {
            HttpHeaders response = new HttpHeaders();
            response.set("Content-Location", "No person @ID " + id);

            return ResponseEntity
                    .notFound()
                    .headers(response)
                    .build();
        }
    }

    @Override
    public ResponseEntity<List<ContactDTO>> getContactByPersonId(Long id) {
        Optional<Person> optional = personRepository.findById(id);
        Person person = optional.orElse(null);
        if (person != null) {
            List<Contact> contacts = person.getContacts();
            List<ContactDTO> contactsResponse = new ArrayList<>();
            contacts.stream().forEach(
                c -> contactsResponse.add(modelMapper.map(c, ContactDTO.class))
            );

            return ResponseEntity
                    .ok(contactsResponse);
        } else {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // TODO: Make this
    @Override
    public ResponseEntity<Void> createContactByPersonId(Long id, ContactDTO contactDTO) {
        Optional<Person> optional = personRepository.findById(id);
        Person person = optional.orElse(null);
        if (person != null) {
            String email = contactDTO.getEmail();
            String phone = contactDTO.getPhone();

            Optional<Contact> checkEmail = contactRepository.findByEmail(email);
            Optional<Contact> checkPhone = contactRepository.findByPhone(phone);

            Contact checkEmailContact = checkEmail.orElse(null);
            Contact checkPhoneContact = checkPhone.orElse(null);

            if (checkEmailContact != null || checkPhoneContact != null) {
                HttpHeaders response = new HttpHeaders();
                String s;
                if (checkEmailContact != null && checkPhoneContact != null) {
                    s = "Both email and phone already exist";
                } else if (checkEmailContact != null) {
                    s = "Email: " + checkEmailContact.getEmail() + " already exists";
                } else {
                    s = "Phone: " + checkPhoneContact.getPhone() + " already exists";
                }
                response.set("Content-Location", s);

                return ResponseEntity
                        .status(HttpStatus.METHOD_NOT_ALLOWED)
                        .headers(response)
                        .build();
            } else {
                Contact contact = modelMapper.map(contactDTO, Contact.class);
                contact.setPersonId(id);
                contactRepository.save(contact);

                HttpHeaders response = new HttpHeaders();
                response.set("Content-Location", "/person/" + id + "/contacts");

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .headers(response)
                        .build();
            }
        } else {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Void> updateContactByPersonId(Long id, List<ContactDTO> contacts) {
        Optional<Person> optional = personRepository.findById(id);
        Person person = optional.orElse(null);
        if (person != null) {
            contactRepository.deleteAllInBatch(person.getContacts());

            List<Contact> personContacts = new ArrayList<>();
            contacts.stream().forEach(
                c -> {
                    Contact contact = modelMapper.map(c, Contact.class);
                    contact.setPersonId(id);
                    personContacts.add(contact);
                }
            );

            contactRepository.saveAll(personContacts);

            HttpHeaders response = new HttpHeaders();
            response.set("Content-Location", "Contact updated @PersonID " + id);

            return ResponseEntity
                    .noContent()
                    .headers(response)
                    .build();
        } else {
            HttpHeaders response = new HttpHeaders();
            response.set("Content-Location", "No person @ID " + id);

            return ResponseEntity
                    .notFound()
                    .headers(response)
                    .build();
        }
    }
}
