package io.zipcoder.crudapp.controllers;

import io.zipcoder.crudapp.models.Person;
import io.zipcoder.crudapp.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PersonController {


    private PersonRepository repo;

    @Autowired
    public PersonController(PersonRepository repo){
        this.repo = repo;
    }

    @PostMapping(value = "/people")
    public ResponseEntity<Person> postPerson(@RequestBody Person p){
        return new ResponseEntity<>(repo.save(p), HttpStatus.CREATED);
    }

    @GetMapping(value = "/people/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable Integer id){
        Person person = repo.findOne(id);
        if (person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @GetMapping(value = "/people")
    public ResponseEntity<Iterable<Person>> getPeople(){
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    @PutMapping(value = "/people")
    public ResponseEntity<Person> putPerson(@RequestBody Person p){
        Person ogPerson =  repo.findOne(p.getId());

        if (ogPerson == null){
            return new ResponseEntity<>(repo.save(p), HttpStatus.CREATED);
        }

        ogPerson.setFirstName(p.getFirstName());
        ogPerson.setLastName(p.getLastName());
        return new ResponseEntity<>(repo.save(ogPerson), HttpStatus.OK);
    }

    @DeleteMapping(value = "/people/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Integer id){
    Person person = repo.findOne(id);

        if (person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        repo.delete(person);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
