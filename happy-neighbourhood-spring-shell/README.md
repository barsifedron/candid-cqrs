# happy-neighbourhood-spring-shell

A working spring project running the happy-neighbourhood-app

## Goals :
1. Illustrate how to interact with a CQRS architecture from your Spring project
2. I went an extra mile to prove that the domain code can exist outside of any Sping (or other framework). The Spring shell is really really thin here. This is by design.  
In practice I might not be that extreme in a real project.

## Disclaimer :

This is obviously a small illustrating project but it does already quite a few things. If you feel like something would be worth illustrating here, please let me know.

## To run:
./gradlew bootRun

You can then open http://localhost:8080/index.html in your browser. There is currently no ui.

## To test endpoints:

Two ways to do so : Using postman or manually with curl.

### with postman:


The repository contains a collection of endpoints that you can import on your Postman.
All you need to do is import this file in your postman and edit the endpoints. Change __both__ path and body to replace : 

- {{member-uuid}} by the id of a member you create
- {{item-uuid}} by the id of an item you created

The collection can be found here : https://github.com/barsifedron/candid-cqrs/blob/master/happy-neighbourhood-spring-shell/candid-cqrs.postman_collection.json

Postman is a really convenient utility and can be found here : https://www.postman.com/

### manually with curl :
In the same manner as with the Postman method, you will need to replace the uuids fields based on what you create.

The curl commands can be found here :

https://github.com/barsifedron/candid-cqrs/blob/master/happy-neighbourhood-spring-shell/candid-cqrs-endpoints-samples-curl.txt

## Things to look at :

- The wiring of the commandBus. This is maybe the only tricky part in this whole code base but you are clever and will figure it out.
- Notice how the controllers role is limited to provide I/O to our command buses. How easy would it be to change frameworks one day if you really wanted to.

