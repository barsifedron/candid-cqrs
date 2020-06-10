package com.barsifedron.candid.cqrs.happy.shell.controllers;

import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommand;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewItemCommand;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewMemberCommand;
import com.barsifedron.candid.cqrs.happy.command.ReturnItemCommand;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.query.GetMemberQueryHandler;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoanWorkflowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldCreateNewMember() throws Exception {

        ResponseEntity<String> registerMemberResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/members",
                RegisterNewMemberCommand
                        .builder()
                        .email("john@malkovitch.com")
                        .firstname("john")
                        .surname("malkovitch")
                        .build(),
                String.class
        );
        assertThat(registerMemberResponse.getStatusCode().value()).isEqualTo(201);

        ResponseEntity<String> registerItemResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/items",
                RegisterNewItemCommand
                        .builder()
                        .name("hammer")
                        .dailyRate(new BigDecimal("1.00"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(14)
                        .build(),
                String.class
        );
        assertThat(registerItemResponse.getStatusCode().value()).isEqualTo(201);

        Map<String, String> args = new HashMap<>();
        args.put("memberId", registerMemberResponse.getBody());

        ResponseEntity<String> borrowItemResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/members/{memberId}/loans",
                BorrowItemCommand
                        .builder()
                        .itemId(registerItemResponse.getBody())
                        .build(),
                String.class,
                args);
        assertThat(borrowItemResponse.getStatusCode().value()).isEqualTo(201);

        args = new HashMap<>();
        args.put("itemId", registerItemResponse.getBody());

        System.out.println("args = " + args);

        ResponseEntity<String> returnItemResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/items/{itemId}/return",
                null,
                String.class,
                args);
        assertThat(returnItemResponse.getStatusCode().value()).isEqualTo(200);

        ResponseEntity<GetMemberQueryHandler.MemberDto> getMemberResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/members/" + registerMemberResponse.getBody(),
                GetMemberQueryHandler.MemberDto.class);

        System.out.println("getMemberResponse = " + getMemberResponse);
        assertThat(returnItemResponse.getStatusCode().value()).isEqualTo(200);

       getMemberResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/members/john" ,
                GetMemberQueryHandler.MemberDto.class);

        System.out.println("getMemberResponse = " + getMemberResponse);
        assertThat(returnItemResponse.getStatusCode().value()).isEqualTo(200);


        ResponseEntity<GetMemberQueryHandler.MemberDto> getItemResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/items/" + registerItemResponse.getBody(),
                GetMemberQueryHandler.MemberDto.class);

        System.out.println("getItemResponse = " + getItemResponse);
        assertThat(returnItemResponse.getStatusCode().value()).isEqualTo(200);

        getItemResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/items/hammerId",
                GetMemberQueryHandler.MemberDto.class);

        System.out.println("getItemResponse = " + getItemResponse);
        assertThat(returnItemResponse.getStatusCode().value()).isEqualTo(200);
    }

}