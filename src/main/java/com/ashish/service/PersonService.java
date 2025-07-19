package com.ashish.service;

import javax.persistence.EntityManager;

import com.ashish.repository.PersonRepository;
import jdk.jshell.spi.ExecutionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ashish.entity.Person;

import java.util.List;

@Service
public class PersonService implements ApplicationContextAware {
    @Autowired
    PersonRepository personRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    public void callTransactionalMethodsWithoutTrasaction() {
        LOGGER.info("================ callTransactionalMethodsWithoutTrasaction() =====================");
        try {
            Person person1 = new Person();
            person1.setFirstName("Person1");
            person1.setLastName("Person1");
            person1.setEmail("email1");
            entityManager.persist(person1);
        } catch (Exception e) {
            LOGGER.error("Error in callTransactionalMethodsWithoutTrasaction() : {}", e.getClass());
        }
        required();
        requiresNew();
        nested();
        never();
        supports();
        notSupported();
        nested();
    }

    @Transactional
    public void callTransactionalMethodsWithTrasaction() {
        LOGGER.info("================ callTransactionalMethodsWithTrasaction() ========================");
        try {
            Person person1 = new Person();
            person1.setFirstName("Person1");
            person1.setLastName("Person1");
            person1.setEmail("email1");
            entityManager.persist(person1);
        } catch (Exception e) {
            LOGGER.error("Error in callTransactionalMethodsWithTrasaction() : {}", e.getClass());
        }
        findAll("Before required");
        required();
        findAll("Before requiresNew");
        requiresNew();
        findAll("Before nested");

        nested();
        findAll("Before never");

        never();
        findAll("Before supports");

        supports();
        findAll("Before not supported");

        notSupported();
        findAll("Before nested");

        nested();
        findAll("After nested");
    }

    @Transactional
    public void scenario(int index,
                         boolean exceptionInMethodA,
                         boolean exceptionInMethodB,
                         boolean methodBRequiresNew) {
        System.out.println("PersonService.scenario: start");
        persistPerson(index);
        if (methodBRequiresNew)
            methodRequiresNew(index, exceptionInMethodB);
        else
            methodRequired(index, exceptionInMethodB);
        if (exceptionInMethodA) throw new RuntimeException("exceptionInMethodA");
        System.out.println("PersonService.scenario:end");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequired(int index, boolean exceptionInMethodB) {
        System.out.println("PersonService.methodRequired: start");
        persistPerson(index*10+1);
        if (exceptionInMethodB) throw new RuntimeException("exceptionInMethodB");
        System.out.println("PersonService.methodRequired");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiresNew(int index, boolean exceptionInMethodB) {
        System.out.println("PersonService.methodRequiresNew: start");
        persistPerson(index*10+1);
        if (exceptionInMethodB) throw new RuntimeException("exceptionInMethodB");
        System.out.println("PersonService.methodRequiresNew");
    }

    @Transactional
    public void methodB4() {
        persistPerson(41);
    }

    private void persistPerson(int index) {
        Person person2 = new Person();
        person2.setFirstName("Person" + index);
        person2.setLastName("Person" + index);
        person2.setEmail("email" + index);
        entityManager.persist(person2);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNew() {
        try {
            persistPerson(2);
            LOGGER.info("This is from requiresNew()");
        } catch (Exception e) {
            LOGGER.error("Error in requiresNew() : {}", e.getClass());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void required() {
        try {
            persistPerson(3);
            LOGGER.info("This is from required()");
        } catch (Exception e) {
            LOGGER.error("Error in required() : {}", e.getClass());
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void mandatory() {
        try {
            persistPerson(4);
            LOGGER.info("This is from mandatory()");
        } catch (Exception e) {
            LOGGER.error("Error in mandatory() : {}", e.getClass());
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void nested() {
        try {
            persistPerson(5);
            LOGGER.info("This is from nested()");
        } catch (Exception e) {
            LOGGER.error("Error in nested() : {}", e.getClass());
        }
    }

    @Transactional(propagation = Propagation.NEVER)
    public void never() {
        LOGGER.info("This is from never()");
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void notSupported() {
        LOGGER.info("This is from notSupported()");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void supports() {
        try {
            persistPerson(8);
            LOGGER.info("This is from supports()");
        } catch (Exception e) {
            LOGGER.error("Error in supports() : {}", e.getClass());
        }
    }

    public List<Person> findAll(String situation) {
        LOGGER.info("*".repeat(10));
        LOGGER.info(situation);
        personRepository.findAll().forEach(System.out::println);
        return personRepository.findAll();
    }

    private ApplicationContext applicationContext;
    private EntityManager entityManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.entityManager = this.applicationContext.getBean(EntityManager.class);
    }

}
