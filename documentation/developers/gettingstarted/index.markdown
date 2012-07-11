---
layout: documentation
title: Getting started
---

Getting Started for developers
============

As the project is an early stage version, you will need to develop using the whole kernely project.

Requirements
------------

In order to build the project, you will need the following things

- Java 1.6
- Maven 3+


Installation
------------

- Clone the repository from [github](https://github.com/prometil/kernely)
- Do a mvn clean install in the kernely/kernely directory, and let maven grab the web.
- Import the project in your favorite IDE (NB : if you use eclipse, you can do a ___mvn eclipse:eclipse___, and then import the project)

Run
---
As each project is a module, the kernely project is able to run as a standalone server. Just run the class ___org.kernely.Kernely___ and that's all about it!

If you want to test you plugin just link your project to the kernely project, so that it will be in the kernely project classpath. Kernely will grab it and load it at runtime.