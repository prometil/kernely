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
      "name": "timesheet",
      "plugin": "org.kernely.timesheet.TimeSheetPlugin",
      "version": "0.1",
      "author": "kernely.org",
      "description": "Timesheet plugin",
       "configuration": {
          "maxDayValue": "8.0"
      }
  }
{% endhighlight %}



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

The plugin class is a java class that extends ...
