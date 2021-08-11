package com.ogorodnik.hibernate;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryGeneratorTest {
    private Object id;
    QueryGenerator queryGenerator;
    Person person;
    PersonWithoutId personWithoutId;

    @Before
    public void before(){
        queryGenerator = new QueryGenerator();

        person = new Person();
        person.setId(5);
        person.setName("Alex");
        person.setSalary(45.5);

        personWithoutId = new PersonWithoutId();
        personWithoutId.setX(10);
        personWithoutId.setName("Ogorodnik");
        personWithoutId.setSalary(54.4);
    }

    @Test
    public void testGetAll(){
        String getAllSql = queryGenerator.getAll(Person.class);
        String expectedSql = "SELECT id, person_name, salary FROM persons;";
        assertEquals(expectedSql, getAllSql);
    }

    @Test
    public void testGetById(){
        String getByIdSql = queryGenerator.getById(Person.class, id);
        String expectedSql = "SELECT id, person_name, salary FROM persons WHERE id = 4;";
        assertEquals(expectedSql, getByIdSql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWithNoId(){
        String getByIdSql = queryGenerator.getById(PersonWithoutId.class, id);
        String expectedSql = "SELECT id, person_name, salary FROM persons WHERE id = 4;";
        assertEquals(expectedSql, getByIdSql);
    }

    @Test
    public void testDelete(){
        String deleteSql = queryGenerator.delete(Person.class, id);
        String expectedSql = "DELETE FROM persons WHERE id = 4;";
        assertEquals(expectedSql, deleteSql);
    }

    @Test
    public void testInsert() throws IllegalAccessException {
        String insertSql = queryGenerator.insert(person);
        String expectedSql = "INSERT INTO persons (id, person_name, salary) VALUES ('5', 'Alex', '45.5');";
        assertEquals(expectedSql, insertSql);
    }

    @Test
    public void testUpdate() throws IllegalAccessException {
        String updateSql = queryGenerator.update(person);
        String expectedSql = "UPDATE persons SET person_name = 'Ogorodnik', salary = '45.5' WHERE id = 5";
        assertEquals(expectedSql, updateSql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNoId() throws IllegalAccessException {
        String updateSql = queryGenerator.update(personWithoutId);
        String expectedSql = "UPDATE persons SET person_name = 'Alex', salary = '54.4' WHERE x = 10";
        assertEquals(expectedSql, updateSql);
    }
}
