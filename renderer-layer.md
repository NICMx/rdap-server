---
title: RedDog's Renderer Layer
breadcrums: ["Documentation", "documentation.html", "Rendering Response Data", "documentation.html#rendering-response-data"]
wheretogo: ["Create a new RDAP-renderer implementation", "renderer-implementation.html"]
---
# {{ page.title }}

## Index

1. [Introduction](#introduction)
1. [Configuring `renderers.properties`](#configuring-renderersproperties)
   1. [`renderers`](#renderers)
   1. [`{renderer_name}.class`](#renderer_nameclass)
   1. [`{renderer_name}.main_mime`](#renderer_namemain_mime)
   1. [`{renderer_name}.mimes`](#renderer_namemimes)
   1. [`default_renderer`](#default_renderer)
1. [Example](#example)
1. [Adding a renderer-implementation](#adding-a-renderer-implementation)

## Introduction

The RedDog RDAP server, after forming the response of a user request, delegates the responsibility of rendering the result to an implementation of the [**rdap-renderer-api**](https://github.com/NICMx/rdap-renderer-api).

The implementation is chosen based on the MIME type (Content-type) requested by the user at the time of the request. This implementation(s) should be configured at [`WEB-INF/renderers.properties`](https://github.com/NICMx/rdap-server/blob/master/src/main/webapp/WEB-INF/renderers.properties) configuration file.

First, the server gets the MIME type requested by the user, it is checked in the `WEB-INF/renderers.properties` configuration if the MIME type requested by the user is mapped to some renderer implementation.

If the MIME type is mapped, the response will be rendered with the configured implementation, otherwise, if the MIME type requested by the user does not exist in the configuration, a default implementation that has been configured for any unregistered MIME type is chosen.

The RedDog team creates two reference implementations of **rdap-renderer-api**:

+ __[rdap-json-renderer](https://github.com/NICMx/rdap-json-renderer)__, this renderer prints the output of the requests in the JSON format as indicated by [RFC 7483](https://tools.ietf.org/html/rfc7483).
	+ This renderer comes loaded and configured on the RDAP-server. 
+ __[rdap-text-renderer](https://github.com/NICMx/rdap-text-renderer)__, this renderer prints the output in plain text, in a format similar to WHOIS responses.
	+ You need to add the jar and configure this renderer in order to use it.
	

## Configuring `renderers.properties`

To tell the RDAP RedDog server which renderer implementations to use, the `WEB-INF/renderers.properties` file must be configured.

Here's how this property file should be configured.

### `renderers`

List of names of the renderers to be configured, each name will be separated by a comma, and each name should not have space.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![Yes](img/green_bkg_check.svg) | String (or List separated by a comma) | NO default value | renderers = json |

For each name configured in the renderers property, it is necessary to configure other attributes (__{renderer_name}.*__) explained below.

### `{renderer_name}.class`

Indicates the renderer's rendering class, and will be used by the server to create an instance of that renderer class.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![Yes](img/green_bkg_check.svg) | String | ![No](img/red_x.svg) | json.class = mx.nic.rdap.renderer.json.JsonRenderer |


### `{renderer_name}.main_mime`

Indicates that the MIME type will be mapped to the renderer indicated in the .class attribute, in addition this MIME type will be added to the headers of the server response as the MIME type used to respond.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![Yes](img/green_bkg_check.svg) | String | ![No](img/red_x.svg) | json.main_mime = application/rdap+json |

```
The structure of a MIME type is very simple; it consists of a type and a subtype, 
two strings, separated by a '/'. No space is allowed. The type represents the category 
and can be a discrete or a multipart type. The subtype is specific to each type.

A MIME type is insensitive to the case, but traditionally is written all in lower case.
```

### `{renderer_name}.mimes`

List of MIME types separated by commas, these MIME types will also be mapped to the renderer indicated in the .class attribute, but unlike .main_mime, these MIME types will not be published in the Server response headers.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | String | ![No](img/red_x.svg) |  json.mimes = application/json, application/html |

### `default_renderer`

Sets the renderer name to act as the default renderer for any MIME type. The name must be one of the configured in the renderers property.

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![Yes](img/green_bkg_check.svg) | String | ![No](img/red_x.svg) | default_renderer = json |

## Example

The following is an example configuration of 'renderers.properties' and a table that demonstrates the behavior of the example configuration

```ini
renderers = json, html, text

json.class = foo.bar.json.JsonRenderer
json.main_mime = application/json+rdap
json.mimes = application/json


html.class = net.example.html.HtmlRenderer
html.main_mime = text/html
html.mimes = application/html, text/xml, application/xml

text.class = com.example.TextRenderer
text.main_mime = text/plain

default_renderer = json

```

| MIME type requested | Renderer | MIME type sent by the server |
|---------------------|----------|------------------------------|
| application/json | json | application/json+rdap |
| application/json+rdap | json | application/json+rdap |
| text/xml | html | text/html |
| application/html | html | text/html |
| text/html | html | text/html |
| text/plain | text | text/plain |
| application/plain | json (default) | application/json+rdap |
| text/csv | json (default) | application/json+rdap |


## Adding a renderer implementation

Below are the steps to add a new renderer to the RDAP server.

In this case an implementation offered by the RedDog team will be added, this is the **text-renderer**. These steps will be the same to add any other implementation(s).

1. Obtain the JAR of a renderer implementation.
1. Copy the JAR of the renderer implementation inside the RDAP-server application, in the `WEB-INF/lib` folder. 
1. Configure the file `renderers.properties` so that the RDAP-server detects the new renderer. 
	1. Add to the property "**renderers**" the name of the new renderer to be added, in this case "**text**".
	1. Add the "**text.class**" property with the following value "**mx.nic.rdap.renderer.text.TextRenderer**" which is the class that implements the Renderer interface.
	1. Add the "**text.main_mime**" property with the value "**text/plain**" so that the text renderer responds to requests that request that MIME type.
	1. As an optional configuration, the “**text.mimes**” property can be configured with other MIME Types that the Text renderer will accept.
	
To know the meaning of each property in the `renderers.properties` file, click [here](#configuring-renderersproperties)

The following block shows how the `renderers.properties` file should look like with the text-renderer and the json-renderer configured.

```ini
renderers = json, text

json.class = mx.nic.rdap.renderer.json.JsonRenderer
json.main_mime = application/rdap+json
json.mimes = application/json

text.class = mx.nic.rdap.renderer.text.TextRenderer
text.main_mime = text/plain

default_renderer = json

```