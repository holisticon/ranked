# Ranked
Application for tracking table-soccer results. 

## Side Goals

- Learning of SpringBoot with Kotlin
- Learning of CQRS, Event Sourcing with AxonFramework
- Playing with a chat bot and understanding the UX
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

## History

Wait. Last time I was here you spoke about implementing it in Scala. Right, the initial idea was to implement the application 
in Scala using the JEE stack. This idea has remained idea after the prototype implementation of basic aspects like persistence with JPA, 
some JEE Beans and controllers. After several years, we decided to try it again...

If you are still interested, check out the [legacy-scala](https://github.com/holisticon/ranked/tree/legacy-scala) branch.



## Decisions

### Test framework

Though using plain junit/assertj unit tests would be possible, we want to try the kotlin way.

