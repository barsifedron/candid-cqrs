# bus-cqrs-api

A really simple cqrs-bus example that you can use as a basis for your projects.

Points of interests :

- In the bus-cqs project we directly decorated command buses. Here we use the middleware pattern (slightly more complex, slightly more easy to instantiate). The principle remains the same and both solutions are valid.
- Now the command handlers return list of "domain events"
- Those events will then be passed to the Event Bus to be dispatched to the matching event handlers
- Your command handlers should focus on domain changes (understand "One aggregate change per transaction") while the DomainEventHandlers should deal with your "side effects" (update a counter, schedule the sending of an email etc...). Again, refer to the presentations that inspired this project.