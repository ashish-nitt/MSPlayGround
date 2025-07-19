package com.ashish.controller;

import com.ashish.entity.Person;
import com.ashish.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SampleController {
    @Autowired
    PersonService personService;

    @PostMapping(value = {
            "scenario/{scenarioIndex}",
            "scenario/{scenarioIndex}/{exceptionInMethodA}",
            "scenario/{scenarioIndex}/{exceptionInMethodA}/{exceptionInMethodB}",
            "scenario/{scenarioIndex}/{exceptionInMethodA}/{exceptionInMethodB}/{methodBRequiresNew}"})
    public List<Person> runScenario(@PathVariable("scenarioIndex") Integer scenarioIndex,
                                    @PathVariable(value = "exceptionInMethodA", required = false) Boolean exceptionInMethodA,
                                    @PathVariable(value = "exceptionInMethodB", required = false) Boolean exceptionInMethodB,
                                    @PathVariable(value = "methodBRequiresNew", required = false) Boolean methodBRequiresNew) {

        //personService.callTransactionalMethodsWithoutTrasaction();
        //personService.callTransactionalMethodsWithTrasaction();


        personService.findAll("Before Call");
        try {
            personService.scenario(scenarioIndex,
                    exceptionInMethodA != null && exceptionInMethodA,
                    exceptionInMethodB != null && exceptionInMethodB,
                    methodBRequiresNew != null && methodBRequiresNew);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return personService.findAll("After call");
    }
}
