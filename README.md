# Motivations #
DB EMF is clearly an alternative to CDO: after using CDO for 2 years on industrial projects, I found out that CDO is really great, and has so much functionnalities (auditing, offline repositories, ...). But as a drawback I had no way to get 100% of the Relationnal Database performances (even with custom mapping, and so....).

DB EMF use a very close mapping than CDO, then you can migrate from already existing CDO database (if based on DBStore).

I like EMF very much, I used to work with it for about 10 years. Now I cannot do without it: you don't have to write POJO, you rather draw Ecore Diagrams, and it generates the code with so many facilities: advanced reflexivity, synchronized diagram and code, visitor pattern, and also many stuffs for UI: !LabelFactory, !ContentProvider, and so on... In that way, DB EMF generates out-of-the-box Java Classes from Ecore Models (specific genmodel is generated) like EMF & CDO does.

Everybody will say that it is yet another Object Relationnal Mapping, and I will answer: not really. Indeed, I have simply dropped all functionnalities that are not compliant with a relationnal database. I wanted to write the thinnest layer between RDB (such as MySQL) and EMF. When running the application (it can only be done at runtime, not at generation time), DB EMF diagnose the meta model to ensure it is compatible with a smooth relationnal mapping. You can check the [Limitations] to know what is supported or not.

Some people would simply say: 

* Erk, so many limitations, don't wanna ear about that...
And I'll answer:
* Maybe but I totally assume that: I fought very hard during all the development for leaving DB EMF very close from a relationnal database. Unfortunatly yes: it may enforce you to redraw some part of your model. But, I consider that we need few efforts to get almost 100% of the power of a Relational Database Engine (Object Databases are still not as fast as a relationnal database, and will probably never since it manage more things still...).
* So what is the difference with a Java Database Framework such as JOOQ, QueryDSL, and so on ?
* It just does not use EMF. And so far JOOQ is the only one to let you map tables with POJOs. It is why I dedicated a section for the comparison DB EMF and jOOQ

# Differences with JPA, POJO (such as Hibernate, EclipseLink) #
TODO

# Differences with [jOOQ](http://www.jooq.org/) #
The vision I really share with jOOQ is the following:
jOOQ claims that generic mapping strategies lead to an enormous additional complexity that only serves very few use cases

I totally agree, and I was really tempted to use JOOQ (even if I use it for another project), **BUT** if CDO manage too many things, jOOQ manage too few things (IT guys are never happy). You can really see DB EMF in the middle of jOOQ and CDO (in the spirit), but it does not represent the same line of code :)

At the current development stage (because jOOQ goes very quickly), my problems with jOOQ are:

* It does not support 0..n relations (I think it should be possible quickly anyway), which is really rude... definitetly too low level for me.
* no synced Ecore diagram
* no caching stuff
* no EMF facilities at all (visitor, !LabelProvider, ...)
* no DB migration mecanism (when changing DB schema)
* You need to write some strange hashCode() and equal() method that does not fit to everyone. DB EMF works exactly the same than CDO and its view, except that it have only one view to simplify.
* Does not support the magic inverseAdd/inverseRemove in EMF: it deals with the eOpposite eReference: when you modify one, it modify the other too. It is probably the reason why jOOQ hasn't implemented 0..n relations. 
Finnally, I had to migrate an EMF application into POJO application, which was really hudge (the opposite should be easier thought).

# Differences with [EMF](http://www.eclipse.org/modeling/emf/) #
* Does not garantuee list ordering
* Only SET Notification events are raised (not ADD, neither REMOVE, but raise the SET event if the EOpposite is set (see the limitations)

# Differences with [CDO](http://wiki.eclipse.org/CDO) #
As I said CDO is really great, but unfortunaly, all its functionnalities requires a more complex mapping with a relationnal databse, and then **performances issues** soon raise. This is basically the main motivation to 'fork' this project: it has a more 'industrial' approach.
Here are the detailed reasons why the DB EMF take over the CDO implementation (of course when using a relationnal database, since DB EMF does not support anything else):

* no marshaling through Net4j
* no auditing and branching functionality
* Does not garantuee list ordering
* the java type of cdo ids is long (and not instances of CDOID)
* there are no id providers
* there are no id mapper
* Does not handle notifications (IListener from CDO), even if you can plug a notification mecanism easily. I am not kind of benchmarking, so I will not give you any precise benchmark because it depending on how much you read/write/use cache/... But in our application, and jUnit, it seems that globaly, the response time looks like multipled by 2. The best thing it that you give it a try.
Appart from that, there are minor facilities that simplify deployment:

* Offers a simple but robust migration mecanism
* Requires to run a CDOServer on a machine (more complex to deploy for 'not IT expert' people). Thought, that doesn't prevent you (like in my case) to implement a server to implement notification mecanism for example (I personnaly use WebSockets). DB EMF can be easily extended to notify other clients.

# Differences with [cdo-light](http://code.google.com/a/eclipselabs.org/p/cdo-light/) #
* Seems to be a nice project, but no getting started, no technical documentation :(

# Differences with [emf-fragment](https://github.com/markus1978/emf-fragments) #
* Suitable for big models/big data, but not for 'small' databases
* Requires NoSQL

# Differences with [NEO4EMF](http://neo4emf.com/) #
* Seems nice project, documented, but did not managed to make it work on Eclipse Kepler (broke my Eclipse :( )
* Requires Neo4J Database

# Limitations and restrictions #
* EFeatureMap are not supported (EMF concept)
* Picture to show what you can model or not in EMF with this light implementation: 

![limitations.png](https://bitbucket.org/repo/KKLrdR/images/2416734542-limitations.png)

# Licensing #
DB EMF is released under EPL license.