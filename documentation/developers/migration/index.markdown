---
layout: documentation
title: Use migrations
---

# Migration

The migration allows you to define a migration for each version of your plugin.


## Define a migration

A migration extends the ___org.kernely.migrator.Migration___ and implements the ___getList___ method to returns a set of commands to run.

{% highlight java %}
package org.kernely.timesheet.migrations;

import java.util.ArrayList;
import java.util.List;
import org.kernely.migrator.Migration;
import org.kernely.migrator.Command;

public class Migration01 extends Migration {
  
  public Migration01() {
    super("0.1");
  }

  @Override
  public List<Command> getList() {
    ArrayList<Command> commands = new ArrayList<Command>();
    return commands;
  }
}
{% endhighlight %}

## Create a table command

The create table command is a simple command to defines an sql table. It provides you with two methods :

- The ___name___ method that generates a CreateTable instance

{% highlight java %}
CreateTable mytable = CreateTable.name("my_table");
{% endhighlight %}

- the ___column___ method to define a new column

{% highlight java %}
  mytable.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
{% endhighlight %}

    
## insert command


The insert command has two methods:

- A ___into___ to specify the table. It returns a new Insert instance

{% highlight java %}
  Insert i = Insert.into("mytable");
{% endhighlight %}

- A ___set___ to specify the values associated with columns

{% highlight java %}
  i.set("mycolumn", "myvalue");
{% endhighlight %}

## delete command

The delete command has two methods:

- A ___from___ to specify the table. It returns a new DeleteCommand

{% highlight java %}
  DeleteCommand d = DeleteCommand.from("mytable");
{% endhighlight %}

- A ___where___ to specify the condition

{% highlight java %}
  DeleteCommand d = DeleteCommand.from("mytable").where("a = b");
{% endhighlight %}


## Update command

- A ___table___ to specify the table. It returns a new Update instance

{% highlight java %}
  Update i = Update.table("mytable");
{% endhighlight %}

- A ___set___ to specify the values associated with columns

{% highlight java %}
  u.set("mycolumn", "myvalue");
{% endhighlight %}

- A ___where___ to specify a condition

{% highlight java %}
  u.where("a = b");
{% endhighlight %}

## Raw sql command

The raw sql command is here to add some custom sql command you may need :

{% highlight java %}
RawSql holidayDonationTypeForeignKey = new RawSql("ALTER TABLE kernely_holiday_donation ADD CONSTRAINT fk_holiday_donation_type FOREIGN KEY (holiday_type_instance_id) REFERENCES kernely_holiday_type_instance(id)");

{% endhighlight %}

## Register a migration

Once your migration file has bee created, you need to call  the registerMigration method in the plugin constructor. It will inform kernely that the  plugin embed a migration system.

{% highlight java %}
registerMigration(new Migration01());
{% endhighlight %}

When kernely starts, it register all the migration classes and than check in the database if it needs to perform an update.