package com.barsifedron.candid.cqrs.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DoSomethingControllerTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void shouldFailWithInvalidValues() throws Exception {

        Map<String, Integer> args = new HashMap<>();

        args.put("value1", 0);
        args.put("value2", 45);
        ResponseEntity<String> stringResponseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/dosomething/with/{value1}/and/{value2}",
                null,
                String.class
                , args);

        assertThat(stringResponseEntity.getStatusCode().value()).isEqualTo(406);
    }

    @Test
    public void shouldSuceedWithCorrectValues() throws Exception {


        Integer doneCounterValue = this.restTemplate.getForObject(
                "http://localhost:" + port + "/dosomething/done",
                Integer.class);

        Map<String, Integer> args = new HashMap<>();

        args.put("value1", 5);
        args.put("value2", 10);
        assertThat(
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/dosomething/with/{value1}/and/{value2}",
                        null,
                        String.class
                        , args))
                .isEqualTo("DONE!");

        args.put("value1", 3);
        args.put("value2", 6);

        assertThat(
                this.restTemplate.postForObject(
                        "http://localhost:" + port + "/dosomething/with/{value1}/and/{value2}",
                        null,
                        String.class
                        , args))
                .isEqualTo("DONE!");

        assertThat(
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/dosomething/done",
                        Integer.class))
                .isEqualTo(doneCounterValue + 2);
    }

}