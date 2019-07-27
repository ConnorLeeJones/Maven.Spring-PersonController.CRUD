package io.zipcoder.crudapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zipcoder.crudapp.models.Person;
import io.zipcoder.crudapp.repositories.PersonRepository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MockitoTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;


    private List<Person> people = Arrays.asList(
            new Person("Connor", "Jones", 1),
            new Person("Test", "Person", 2));


    @Test
    public void testFindAll() throws Exception {


        when(personRepository.findAll()).thenReturn(people);

        mockMvc.perform(get("/people"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Connor")))
                .andExpect(jsonPath("$[0].lastName", is("Jones")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Test")))
                .andExpect(jsonPath("$[1].lastName", is("Person")));

        verify(personRepository, times(1)).findAll();
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void testFindById() throws Exception {
        Person person = new Person("Connor", "Jones", 1);

        when(personRepository.findOne(1)).thenReturn(person);

        mockMvc.perform(get("/people/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Connor")))
                .andExpect(jsonPath("$.lastName", is("Jones")));


        verify(personRepository, times(1)).findOne(1);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void testNotFound() throws Exception {
        when(personRepository.findOne(1)).thenReturn(null);

        mockMvc.perform(get("/people/{id}", 1))
                .andExpect(status().isNotFound());

        verify(personRepository, times(1)).findOne(1);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void testCreate() throws Exception {
        Person newPerson = new Person("Connor", "Jones", 5);

        when(personRepository.save(newPerson)).thenReturn(newPerson);

        mockMvc.perform(
                post("/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newPerson)))
                .andExpect(status().isCreated());

        verify(personRepository, times(1)).save(refEq(newPerson));
        verifyNoMoreInteractions(personRepository);
    }


    @Test
    public void testDelete() throws Exception {
        Person person = new Person("Connor", "Jones", 29);

        when(personRepository.findOne(person.getId())).thenReturn(person);
        doNothing().when(personRepository).delete(person.getId());

        mockMvc.perform(
                delete("/people/{id}", person.getId()))
                .andExpect(status().isNoContent());

        verify(personRepository, times(1)).findOne(refEq(person.getId()));
        verify(personRepository, times(1)).delete(person);
        verifyNoMoreInteractions(personRepository);
    }


    @Test
    public void testDeletePersonNotFound() throws Exception {
        Person person = new Person("Not", "Found", 0);

        when(personRepository.findOne(person.getId())).thenReturn(null);

        mockMvc.perform(
                delete("/people/{id}", person.getId()))
                .andExpect(status().isNotFound());

        verify(personRepository, times(1)).findOne(person.getId());
        verifyNoMoreInteractions(personRepository);
    }


    @Test
    public void testUpdateExistingPerson() throws Exception {
        Person person = new Person("Connor", "Jones", 29);


        when(personRepository.findOne(person.getId())).thenReturn(person);
        //doNothing().when(personRepository).save(person);

        mockMvc.perform(
                put("/people", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isOk());

        verify(personRepository, times(1)).findOne(person.getId());
        verify(personRepository, times(1)).save(person);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void testUpdateNewPerson() throws Exception {
        Person person = new Person("Not", "Found", 0);


        when(personRepository.findOne(person.getId())).thenReturn(null);

        mockMvc.perform(
                put("/people", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isCreated());

        verify(personRepository, times(1)).findOne(person.getId());
        verify(personRepository, times(1)).save(refEq(person));
        verifyNoMoreInteractions(personRepository);
    }


    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

