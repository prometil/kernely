---
layout: documentation
title: Create and deploy plugins
---


Plugins definition and development
==================================


Manifest file
-------------

The main part of a plugin is the ___plugin.json___ file which contains the description of the plugin.

{% highlight json %}
  {
      "name": "myplugin",
      "plugin": "org.mycompany.MyPlugin",
      "version": "0.1",
      "author": "myself",
      "description": "Myplugin",
       "configuration": {
          "maxDayValue": "8.0"
      }
  }
{% endhighlight %}

This configuration is automatically loaded in an abstract configuration instance, that is injectable using git.

<table class="table table-condensed">
      <thead>
        <tr>
          <th>
            Attribute
          </th>
          <th>
            Description
          </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td >
            name
          </td>
          <td >
            The name of the plugin use for the menu
          </td>
        </tr>
        <tr>
          <td >
            plugin
          </td>
          <td >
            The plugin class to use to activate the plugin
          </td>
        </tr>
        <tr>
          <td >
            version
          </td>
          <td >
            The version of the plugin (will be used in further version to match mandatory plugin)
          </td>
        </tr>
        <tr>
          <td >
            author
          </td>
          <td >
            The author of the Plugins
          </td>
        </tr>
        <tr>
          <td >
            configuration
          </td>
          <td >
            A json object containing a hierarchical configuration that will be passed to the plugin
          </td>
        </tr>
      </tbody>
    </table>

Plugin class
------------

The following section will guide you through the plugin class creation.

### Implement the plugin class

The plugin class is a java class that extends ___org.kernely.plugin.AbstractPlugin___. 

This class contains all the element to register your plugins part and extends ___com.google.inject.AbstractModule___.

You need to create a plugin class and register it in the manifest files.


{% highlight java %}
package com.mycompany.myplugin;

import org.kernely.plugin.AbstractPlugin;

/**
 * Plugin for timesheet
 */
public class MyPlugin extends AbstractPlugin {

  public static final String NAME = "myplugin";

  /**
   * Default constructor
   */
  public MyPlugin() {
    super();
    registerName(NAME);
    
  }

  @Override
  public void start() {

  }

  /**
   * Configure the plugin
   */
  @Override
  public void configurePlugin() {
  
  }

}

{% endhighlight %}

### Perform action at plugin bootstrap

The ___start___ method provides you with a simple way to handle the start of the plugin. When kernely is calling start() on you plugin, the dependency injection has been perform even on the plugin. 

### Use dependency injection

In order to use dependency injection in the project, you can bind classes using directly the guice api inside the method ___configurePlugin___

For example, you can bind an instance of MyService as a singleton like this

{% highlight java %}
public void configurePlugin(){
  bind(MyService.class).in(Singleton.class);
}
{% endhighlight %}

