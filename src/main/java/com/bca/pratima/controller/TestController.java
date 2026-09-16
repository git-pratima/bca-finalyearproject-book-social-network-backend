package com.bca.pratima.controller;

import com.bca.pratima.entity.Test;
import com.bca.pratima.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @PostMapping(value = "/refresh")
    public String test(){
        testRepository.deleteAll();
        Test test = new Test();
        test.setMessage("Test Table Refreshed.");
        test.setLastRefreshDate(new Date());
        testRepository.save(test);
        return "Test Table Refreshed.";
    }

}
