# Ranked

Application for tracking table-soccer results.

## Running

* start the h2Application
* start the RankedApplication

* create new Match using localhost:8080/  or the /swagger-ui.html 

## Side Goals

- Learning of SpringBoot with Kotlin
- Learning of CQRS, Event Sourcing with AxonFramework
- Learning "you name it" frontend technology

## Ideas and Requirements

- As a player, I want to enter the results of the match
- As a player, I want to use chat bot for entering the data
- As a player, I want to have a classic UI for entering data
- As a player, I want to know my global ranking (elo-based)
- As a player, I want to know my ranking over the last year

## Development and architecture

We have several input channels and currently unknown algorithms to calculate on data and represent UI. In the same time, 
we know the target domain very well, so we foster domain-driven design and start with CQRS ES architecture style.
Every target algorithm is a separate view on the domain, implemented by a view-projection on the event stream.

We use the following stack:

- SpringBoot 2
- Kotlin
- AxonFramework 3

## Decisions

### Components and project structure

We came up with the following components and project structure for our application. 

- The `application` is a component responsible for launching the entire application. It has dependencies to all other 
components and works as a packaging module for SpringBoot.
- The `h2` is a h2 instance used during development. It provides a in-memory database which can be connected to using the tcp-socket.
- The `command` component contains the core/command part of the CQRS application. It holds the aggregates and the commands and uses AxonFramework. 
- The `axon` component contains additional code required for AxonFramework.
- The `model` component contains the value objects (Player, Team, etc) and the Event-Objects since they are shared between the `command` and the View-components.
- There are a bunch of View-Components, each responsible for a specific use case. The views have no persistence and act as tracking event processors on the stream 
of events stored in teh event store of the application. 
  - The `wall-view` component is displaying the information about played matches. It is comparable with the facebook wall displaying news.
  - The `leaderboard-view` component is calculating the best players and displays those.
  
### API

There are two APIs of the application. The Command API is responsible for accepting user inputs (like recording new matches played). The View API is
offering different views on application data (just following the CQRS pattern). Both APIs are RESTful APIs. If you want explore them, you can use Swagger,
shipped as a part of the application. Just navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) and choose the 
API you want to see in the drop-down menu at the top-right of the screen.

The entire Command API is offered under `/command` resource. The entire View API is offered under `/view` resource. 


### Test framework

Though using plain junit/assertj unit tests would be possible, we want to try the kotlin way.



## History

Wait... Last time I was here you spoke about implementing it in Scala. Right, the initial idea was to implement the application 
in Scala using the JEE stack. This idea has remained idea after the prototype implementation of basic aspects like persistence with JPA, 
some JEE Beans and controllers. After several years, we decided to try it again...

If you are still interested, check out the [legacy-scala](https://github.com/holisticon/ranked/tree/legacy-scala) branch.


## Team 

- Simon Zambrovski
- Jan Galinski
- Daniel Wegener


