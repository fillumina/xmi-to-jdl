## XMI to JDL

This is a very simple project to convert an XMI file produced by the open
source project [Umbrello UML Modeller](https://umbrello.kde.org/) to a JDL file
readable by [JHipster](https://www.jhipster.tech/jdl/).

The importer is very limited and its aim is to allow to design a complex
application by using a simpler visual modeler instead of using the textual
file which I find really difficult to manage if the number of classes grows.

By default all relationships are bidirectional OneToMany and must be defined
in the table that will have the foreign key (FK).

es: {OneToOne with jpaDerivedIdentifier}

Validations can be added on attributes in the comment enclosed by
curly braces {}.

Comments to entities, enums and attributes are duly reported in the JDL.
