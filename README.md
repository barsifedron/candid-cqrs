# candid-cqrs

A simple an accessible CQS/CQRS set of libraries.

[![Build Status](https://github.com/barsifedron/candid-cqrs/workflows/CI/badge.svg)](https://github.com/barsifedron/candid-cqrs/actions)

## Goals :
1. Illustrate the CQS/CQRS usages in a simple and clear manner, even for the beginner
2. Provide a "ready to use" seed that people can use as it is or really easily modify for their own usage
3. Show how CQRS can allow for total framework independence. AKA :  Moving you domain logic from Spring to dropwizard IS simple.
4. Illustrate and insist on the fact that different flavors of CQS/CQRS exist. AKA: You probably do not need event sourcing right now and even a really simple CQS split will greatly help you in your unit testing. (Refer to the link provided for more infos on that).

## Rationale :
During my research on the subject, I could not find a simple enough Java example that would help me fully understand the CQS/CQRS concepts. Not many implementation exist or, really often, they take a highly generalized, highly functional and/or straight to event sourcing approach. My experience is that those can be terrible to read for the beginner and confuse more than illustrate the underlying concepts.

## Guiding principles:
1. No hidden magic, no weird annotations, no over-engineered generic abstract classes, no dark voodoo.
2. "Repetition is better than the wrong abstraction" - Sandi Metz.
3. "Get simple or die trying" - 50 cents, the untold quotes.
4. There might be better ways to do it. Still, nobody will be fired for doing it this way.


## How to start

### If you are a newbie (Some of this stuff is in progress):
 1. Looks at the linked presentations slides.
 2. Checkout the bus-cqs project. As an exercise, add unit tests and a decorator for each of the command/query buses. That should give you an idea of what happens.
 3. Look at bus-cqrs-api and bus-cqrs-example projects.
 4. Looks at the [TO_DO] application domain code and the various illustrated use cases.
 5. Look at the various frameworks integrations of the above domain logic.
 6. Add your own spices to it.


## Sources of inspiration :

Those 3 great presentations : 

https://speakerdeck.com/lilobase/ddd-and-cqrs-php-tour-2018

https://www.slideshare.net/rosstuck/command-bus-to-awesome-town

https://speakerdeck.com/lilobase/cqrs-fonctionnel-event-sourcing-and-domain-driven-design-breizhcamp-2017

## Recommended reads

To be done
