# candid-cqrs

A simple an accessible CQS/CQRS set of libraries.

[![Build Status](https://github.com/barsifedron/candid-cqrs/workflows/CI/badge.svg)](https://github.com/barsifedron/candid-cqrs/actions)

## Goals :
1. Illustrate the CQS/CQRS usages in a simple and clear manner, even for the beginner
2. Provide a "ready to use" seed that people can use as it is or really easily modify for their own usage
3. Show how CQRS can allow for total framework independence. AKA :  Moving you domain logic from Spring to dropwizard IS simple.
4. Illustrate and insist on the fact that different flavors of CQS/CQRS exist. AKA: You probably do not need event sourcing right now and even a really simple CQS split will greatly help you in your unit testing. (Refer to the link provided for more infos on that).

## Rationale :
During my research on the subject, I could not find a simple enough Java example that would help me fully understand the CQS/CQRS concepts.
Not many implementation exist or, really often, they take a highly generalized, highly functional and/or straight to event sourcing approach. 
My experience is that those are terrible to read for the beginner and confuse more than illustrate the underlying concepts.

## Guiding principles:
1. No hidden magic, no weird annotations, no over-engineered generic abstract classes, no dark voodoo.
2. "Repetition is better than the wrong abstraction" - Sandi Metz.
3. _"Get simple or die trying"_ - 50 cents, the untold quotes.
4. "You use libraries. But frameworks use __you__"
5. There might be better ways to do it. Still, nobody will be fired for doing it this way.

## What do I have to gain?

- Say goodbye to service layers and lasagna code
- An external world proof code base
- Finally! freedom on the read side, a word without the dictatorship of Mappers (You might never come back)
- Testability by use case
- Amazing decoupling
- Better traceability of what happens on your system
- Easy handling of specifications change, especially when it come to side effects


## What are the trade offs?
More classes really, but they should be **way** simpler to deal with

## That's is cute but is it reliable/fast/worth the pain?
I use this architecture in production, not on a _"let's code twitter in 30 seconds"_ toy project but in a payroll app people rely on to receive their money.
They are pretty unforgiving audience and did not complain so far.
Once understood, I find this type of architecture to be a delight to work with and to offer a flexibility I was herdly finding in my projects previously.

Do not to take my word on it. Take it for a spin.

## How to start

### If you are a newbie (Some of this stuff is in progress):
 1. Looks at the linked presentations / slides.
 2. Checkout the cqrs-api project. As an exercise, add unit tests and a decorator for each of the command/query buses. That should give you an idea of what happens.
 4. Looks at the happy-neighbourhood  domain code.
 5. Look at the various frameworks integrations (only Spring is available so far).
 6. Add your own spices to it.


## Sources of inspiration :

Those great presentations : 

https://speakerdeck.com/lilobase/ddd-and-cqrs-php-tour-2018

https://www.slideshare.net/rosstuck/command-bus-to-awesome-town

https://speakerdeck.com/lilobase/cqrs-fonctionnel-event-sourcing-and-domain-driven-design-breizhcamp-2017

Alistair in the hexagone: https://www.youtube.com/watch?v=th4AgBcrEHA

Models & Service Layers; Hemoglobin & Hobgoblins:
https://www.youtube.com/watch?v=ajhqScWECMo

## Recommended reads

To be done but in the meantime

- hexagonal architecture
- Implementing Domain Driven Design

More to come...


## Final word

If you are a beginner, you should __definitely__ stay away from frameworks. Don't even get me started on Lagom.

