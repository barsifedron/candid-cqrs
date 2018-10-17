# bus-cqs

A really simple cqs-bus example that you can use as a basis / inspiration for your projects.
This is a toy project. the goal is only to illustrate the first concepts between the Command/Query segregation
and the interception of commands within the command bus to add complex behaviors.
Once you understand it all you can move to the slightly more complex bus-cqrs project which provides a more ready to work with set of classes.

Points of interests :

- We choose here to instantiate command buses by simple decoration.
- No external libs
- Provides a couple of examples of bus interceptors
- No handling of domain events by the command bus (I said it was simple). See other modules for such examples
- Reminder : you do not need a bus to do CQS.