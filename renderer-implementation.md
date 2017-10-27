---
title: Create a new RDAP-renderer implementation
---

# Create a new RDAP-renderer implementation

RedDog RDAP Server allows to generate new implementations to render the result of a user request.

To generate a new implementation it is necessary to implement the interface __"[mx.nic.rdap.renderer.Renderer](https://github.com/NICMx/rdap-renderer-api/blob/master/src/main/java/mx/nic/rdap/renderer/Renderer.java)"__ found in the project __"[rdap-renderer-api](https://github.com/NICMx/rdap-renderer-api)"__ 

It is necessary to implement all the functions of the interface. Each function serves to render a server response type, and if the function does not exist, the server could throw an exception or even display a blank response to the user.

Each function receives as a parameter a result, which can be of type RequestResponse&lt;T&gt;, SearchResponse&lt;T&gt;, ExceptionResponse and HelpResponse, in addition it receives a PrintWriter.

- The __RequestResponse&lt;T&gt;__ parameter contains information about a specific object, these &lt;T&gt; objects are the objects defined in the RDAP (entity, domain, ns, autnum, ip network) protocol.

- The parameter of type __SearchResponse&lt;T&gt;__, contains the result of a search performed by a user to the server. The result&lt;T&gt; can be a list of entities, domains, or nameservers.

- The parameter of type __ExceptionResponse__, indicates that the server responded with an error to the request of the user. It contains information about the HTTP error code, error title and error description.

- The __HelpResponse__ parameter is the result of a request to the RDAP server's help command.

- Finally, the __PrintWriter__ parameter, present in all functions, is the output stream that will print the text as the implementor wishes.

For example, if a user needs the result of an object to be printed in HTML, the implementer is responsible for writing HTML in the PrintWriter using Java code, and in this way the result can be interpreted in HTML.

## Unit testing of Renderer implementation.

To verify and validate that an implementation does not throw exceptions on the server, a project called __[rdap-renderer-test-api](https://github.com/NICMx/rdap-renderer-test-api)__ was generated.

The idea of __rdap-renderer-test-api__ is to generate unit tests on rdap-renderer-api implementations.

__rdap-renderer-test-api__ helps in generating responses, in a way the server would do it.

The response is generated with test values, and every time a value or an object is added the response is rendered, this also serves to see the renderer behavior against null objects and null values, and prevent the server from throwing exceptions of type NullPointerException when rendering a response.

In the following links you can see how unit tests were implemented on renderer implementations, using the rdap-renderer-test-api.

- [json-renderer](https://github.com/NICMx/rdap-json-renderer/blob/master/src/test/java/mx/nic/rdap/test/json/TestJsonRenderer.java) unit tests
- [text-renderer](https://github.com/NICMx/rdap-text-renderer/blob/master/src/test/java/mx/nic/rdap/renderer/text/TestTextRenderer.java) unit tests