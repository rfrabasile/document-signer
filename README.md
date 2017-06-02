## Synopsis

Tool for signing XML documents using a JKS file (w/private key).

## Use Example

The following example outputs a file called file.xml.output, signed with the jks (does not include certificate):

```
java -jar document-signer-1.0-SNAPSHOT-jar-with-dependencies.jar file.jks password alias file.xml false

```

Outputs a file called file.xml.output, signed with the jks (includes certificate):


```
java -jar document-signer-1.0-SNAPSHOT-jar-with-dependencies.jar file.jks password alias file.xml true

```

## Motivation

This project was created after working for nearly 3 years in e-billing, dealing with the problems of signing e-bills in order to be sent.

## Installation

Generate the jar and use as indicated in the "Use Example".

## License

Project is free to use and modify, intended for academic use. Keeping a reference to this project is appreciated.
