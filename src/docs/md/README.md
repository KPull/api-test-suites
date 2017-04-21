<!--
The README.md file at the root of the repository is automatically generated from the file in 'src/docs/md/README.md'. If you want to submit
a change to the readme, please submit the change by editing the file on `https://github.com/bastion-dev/Bastion/edit/master/src/docs/md/README.md`
-->

![Bastion Logo](https://raw.githubusercontent.com/KPull/Bastion/master/src/docs/logo/bastion_logo.png)

# Overview

Bastion is a library that eases the development of end-to-end tests for HTTP APIs. A typical user would write 
tests using Bastion based off of some sort of API specification. This API specification can be anything like a 
WADL file, RAML file or a JSON schema. A test engineer would prepare requests and responses based on these specifications 
to test the overall process of calling these APIs.

Reference guide can be found on: http://bastion-dev.github.io/Bastion/reference/index.html

JavaDocs are available on: http://bastion-dev.github.io/Bastion/javadocs/index.html

# Usage

## Test

* `GET` Request:
[ex:general-get-example]

* `POST` Request:
[ex:general-post-example]

* JSON Assertion: (property order in response does not affect test)
[ex:simple-json-assertion]

* JSON Request:
[ex:json-request-example]

* JSON Request/Assertion loaded from file:
[ex:load-from-file]

* Form URL Encoded Data Request:
[ex:form-url-encoded-request]

* Bind the response entity to a model object:
[ex:bound-data]

* Groovy (using multi-line strings):
[ex:groovy-example]

## Dependency

Use a dependency management tool such as Maven to download the Bastion library for use in your project. Add the following
dependency to your POM file:
```xml
<dependency>
    <groupId>rocks.bastion</groupId>
    <artifactId>bastion</artifactId>
    <version>[VERSION]</version>
    <scope>test</scope>
</dependency>
```

Alternatively, use Groovy Grapes to use Bastion in your Groovy tests/scripts:
```groovy
@Grapes(
    @Grab(group='rocks.bastion', module='bastion', version='[VERSION]')
)
```

# Building

Use Maven to build Bastion and run the associated tests. After checking out the repository 
use the following command to build and test the source code.

    mvn install

# Contribute

Bastion is an open-source project! Open-source means that we encourage you to contribute in any way you can. We will accept all contributions, in any shape
or form, that help make Bastion better. Here are some things you can do to contribute:

* Send a positive comment to the Bastion contributers. :)
* [Submit an issue](https://github.com/KPull/Bastion/issues) on GitHub containing a bug report or suggestion. We ask you to spend a couple minutes before
  submitting an issue to check that it has not been submitted earlier. When opening an issue, try to include as much detail as possible so that the
  community can more easily address your concern.
* Submit a pull request for any of our [open issues](https://github.com/KPull/Bastion/issues?q=is%3Aopen+is%3Aissue). Some issues are more easy to implement
  than others and, if you're just starting out, these issues let you get used to the Bastion code structure. If you need any assistance, simply comment on
  the issue at hand and we'll be glad to help. We ask that you adhere to a consistent code style and employ good programming practice but don't worry if
  you're unsure about anything: we'll help you get your submission up to scratch as well.
* You can also [submit a pull request](https://github.com/KPull/Bastion/pulls) which is not related to any of the issues currently on GitHub. If you have
  developed your own `Request` or `Assertions` implementations, for example, and you believe they could be useful to the rest of the Bastion community,
  we will add them to the library for use in future versions of Bastion.
* Help us make the front `README` better. If you feel like the `README` is missing information which you think should be there then open a pull request
  with your suggested changes. You can also help us with typos, grammar or anything else you see fit. Note that the front `README`
  file is auto-generated: to submit a change please [edit the `README` source file](https://github.com/bastion-dev/Bastion/edit/master/src/docs/md/README.md).
* Spread the word. Tell your colleagues about Bastion or write a blog post about Bastion. The more people we can tell Bastion about, the better!

