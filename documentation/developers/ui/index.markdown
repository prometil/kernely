---
layout: documentation
title: Controllers and views
---


# Controllers

The whole core of kernely is based on Jersey. It allows a direct generation of JSON and XML

## Defining a controller

To define a controller, you need to extends the ___AbstractController___ class and add a ___@path___ annotation

{% highlight java %}
@Path("/timesheet")
public class TimeSheetController extends AbstractController {

}
{% endhighlight %}

## Register a controller

The ___registerController___ method allows you to make kernely aware of you controller:

{% highlight java %}
public MyPlugin(){
  super();
  registerController(MyController.class);
}
{% endhighlight %}


## Add a menu

To add a Path to the menu you need to add the ___@Menu()___ annotation with a given key. The key is used to internationalize to menu link.

{% highlight java %}
  @GET
  @Menu("timesheet")
  @Produces( { MediaType.TEXT_HTML })
  public Response MyPage() {
    
  }
{% endhighlight %}


#Views

The view system use [soba](https://github.com/octalmind/soba). If you want to render a specific view, you juste need to inject the template renderer:

{% highlight java %}
@Inject
private SobaTemplateRenderer templateRenderer;
{% endhighlight %}

and then call it in your method :

{% highlight java %}
@GET
@Path("/view")
@Produces( { MediaType.TEXT_HTML })
public Response viewTimeSheetPanel() {
  return Response.ok(templateRenderer.render("templates/timesheet_main_page.html")).build();
}
{% endhighlight %}