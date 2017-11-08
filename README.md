# [Red Dog](https://www.reddog.mx/)

Red Dog is an Open Source RDAP (Registration Data Access Protocol) Server implementation built with Java, funded and developed by [NIC Mexico](http://www.nic.mx).

The server is a RESTful service expected to provide HTTP content in accordance with:
- [RFC 7480 - HTTP Usage in the Registration Data Access Protocol (RDAP)](https://tools.ietf.org/html/rfc7480)
- [RFC 7481 - Security Services for the Registration Data Access Protocol (RDAP)](https://tools.ietf.org/html/rfc7481)
- [RFC 7482 - Registration Data Access Protocol (RDAP) Query Format](https://tools.ietf.org/html/rfc7482)
- [RFC 7483 - JSON Responses for the Registration Data Access Protocol (RDAP)](https://tools.ietf.org/html/rfc7483).

Beside the RFCs accordance, Red Dog has the following features:
- Response render can be customized by implementing a set of interfaces; e.g. beside JSON responses, a TEXT/HTML/XML or any other response type can be returned if the implementer wishes to.
- Reference database and data access implementation to ease Red Dog's use.
- A set of Java interfaces to implement any kind of data access according to the implementer needs (e.g. data can be obtained from the implementer data repository).
- Optional Basic Authentication and the possibility to implement/customize the authentication type if needed.
- Response data privacy using general settings (e.g. everybody can see X data, nobody can see Y data, etc.) or specific settings (e.g. only the owner can see X data, certain custom user roles can see Y data).

## Documentation

The documentation can be seen [here](https://www.reddog.mx/documentation.html).

## Contact

Any comment or feedback is welcomed, issues can be reported at Red Dog's [Github corner](https://github.com/NICMx/rdap-server).

Visit our [contact page](https://www.reddog.mx/contact.html) to get more information.

## License

Red Dog is licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).