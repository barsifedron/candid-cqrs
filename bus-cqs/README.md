# bus-cqs

A really simple cqs-bus example that you can use as a basis / inspiration for your projects.

Points of interests :

- 2 different ways to instantiate a command bus. Either by simple decoration or by using the middleware pattern (slightly more complex, slightly more easy to instantiate)
- No external libs
- Provides a couple of examples of bus interceptors
- No handling of domain events by the command bus (I said it was simple). See other modules for such examples
- Reminder : you do not need a bus to do CQS.