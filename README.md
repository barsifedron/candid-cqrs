# candid-cqrs


A simple an accessible CQS/CQRS code example, aimed at :

1. Illustrate the CQS/CQRS usages in a simple and clear manner, even for the beginner
2. Provide a "almost ready to use" seed that people can use as it is or really easily modify for their own usage
3. Provide total framework independence
4. Illustrate and insist on the fact that different flavors of CQS/CQRS exist. AKA: You probably do not need event sourcing right now and even a really simple CQS split will greatly help you in your unit testing. (Refer to the link provided for more infos on that)


Rationale :

During my research on the subject, I could not find a simple enough Java example that would help me fully understand the CQS/CQRS concepts. Not many implementation exist or, really often, they take a highly generalized, highly functional and/or straight to event sourcing approach. My experience is that those are terrible to read for the beginner and confuse more than illustrate the underlying concepts.


Sources of inspiration :

Those 3 great presentations : 

https://speakerdeck.com/lilobase/ddd-and-cqrs-php-tour-2018

https://www.slideshare.net/rosstuck/command-bus-to-awesome-town

https://speakerdeck.com/lilobase/cqrs-fonctionnel-event-sourcing-and-domain-driven-design-breizhcamp-2017
