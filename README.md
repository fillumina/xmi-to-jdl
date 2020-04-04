
# XMI to JDL
Reads an Class Diagram XMI file exported by [Umbrello UML Modeller]
(https://umbrello.kde.org/) and possibly other products compatible with
the format and produces a 
[JHipster](https://www.jhipster.tech/jdl/) JDL output.


## Versions

 . *2.0* 4/4/2020 uses class diagram relationships together with those defined 
in comments

 . *1.0* first version

The multiplicity can be derived from the relationships defined in the class 
diagram itself or by using the relationship comment curly brackets {}
together with validations.

If no relationship multiplicity is specified `ManyToOne` is used by default 
and the owner is the entity containing the actual field (the FK on the DB). 
The relation must be declared only on the owner part. 

Only some of the options described here are parsed, the others will be
just copied in the final JDL, so it's future ready for new options 
to be added to JDL.

Because it can be instructed to honour the private visibility of fields
(by default it does not)
it can be used to translate a java code imported by Umbrello. This is a
very useful workflow because it allows to test the model with POJOs and then
translate it to JDL.

*REMEMBER*: always set the code to 'Java' in Umbrello so that the data types are
set accordingly and you will not found a String entity when you use it
in one of your attributes (Umbrello defaults to C++).

## Entity

The following options (no parameters) can be added in curly braces in the
comment of any Entity (these are all parsed options).

 . `skipClient` doesn't build the client

 . `skipServer` doesn't build the server

 . `filter` adds advanced search filters to the server API

 . `pagination` or `infinite-scroll` pagination types


## Attribute
Attributes can be simple data types (such as String, LocalDate, Integer...)
accepted by JDL or relationships with other entities.

### Data Types

#### Option
Accepts the following options (no arguments):

 . `required`

 . `unique`

 . `display` it's the field to display when referenced
from another entity in a relationship 
(parsed option, there can be only one such field in an entity)

#### Validation
any validation valid for the field type:

 . String:  `minlength(2)`, `maxlength(33)`, `pattern(/[a-zA-z]{7}/)`

 . numbers: `min(1)`, `max(2000)`

 . blobs:  `minbytes(100)`, `maxbytes(2000)`


#### Molteplicity
One of:

 . `ManyToOne` (default if omitted)

 . `OneToMany`

 . `ManyToMany`

 . `OneToOne` eventually followed by <code>with jpaDerivedIdentifier</code>

eventually with `unidirectional` added to each of them;

## Constant

Constants are supported, just use the tag `{substitutions}` in the first
line of a note and put a substitution per line in there with the format:
```
KEY=VALUE
```
There are no quotes and the first `=` separates key and value.

These are some useful substitutions:
```
EMAIL_PATTERN=^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$
SIX_ALPHA=[a-zA-z]{7}
```

JDL supports number constants so MINLENGTH, MAXLENGTH and such can be 
used and initialized like that in the JH file (no support here, must be
added manually):
```
MINLENGTH = 20
```

## Test

The complete graph is available for testing to validate
it and can eventually be changed before producing the JDL.
This must be done programmatically by adding specific code. There is
a kind of pluggable way of acting on it. Testing a graph is 
a very good way to avoid mistakes in case of complex projects with many
entities and relationships.
