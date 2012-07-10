---
layout: documentation
title: Use models
---


# Using models

## Defining a model class

Kernely is using hibernate as a backend to provide database integration. You may find more information on [hibernate site](http://www.hibernate.org/)

## Registering a model

If you want kernely to be aware of you model, you need to add it in you plugin using the registerModel method:

{% highlight java%}
public MyPlugin() {
    super();
    registerModel(MyAwesomeModel.class);
  }
{% endhighlight%}