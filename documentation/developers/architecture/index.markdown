---
layout: documentation
title: Architecture
---


Architecture
============

Kernely is based on four major pieces of open souce software : [Guice](http://code.google.com/p/google-guice/), [jersey](http://jersey.java.net/), [backbone.js](http://backbonejs.org/) and [jetty](http://jetty.codehaus.org/jetty/)

___Jetty___ is used as an embedded webserver, and is launch from the boostrap of the application

___Guice___ is used to bind all the plugins and modules together. Each plugins is a guice module and is loaded by a global injector. This allows us to provide common service by a simple way.

___Jersey___ is here to provide us with the route binding system and controller system.

___backbone.js___ is a MVC framework written in javascript, to struturate the view.


When the server boots up, the guice servlet bind all the plugins together, and bind all the jersey controller on the given path. When a request is made, it is handled by jersey, who dispatch it to the correct controller.


