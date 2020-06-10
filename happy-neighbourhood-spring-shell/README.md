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


## Things to look at :

- The wiring of the commandBus. This is maybe the only tricky part in this whole code base but you are clever and will figure it out.
- Notice how the controllers role is limited to provide I/O to our command buses. How easy would it be to change frameworks one day if you really wanted to.

