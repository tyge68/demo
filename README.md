## Locust Clusters Proxy

This little cli generated with Micronaut SDK aimed to connect multiple locust server as one.

Reasoning behind this, is mainly to control multiple locust server running in different clusters.
Each cluster is itself located in different geographic regions. Final goal for this is to distribute heavy load.

# Build

Use the command ```./mvnw clean package``` to build the jar or ```./mvnw clean package -Dpackaging=docker-native``` for docker

# Usage

Depending how it got build (jar or docker), use the favorite method to start it. The cli will provide the following parameters:

```
Usage: locust-cluster-proxy-cli [-hV] [-t=<targets>[,<targets>...]]...
This server act as a proxy to aggregate data from multple locust manager sources
  -h, --help      Show this help message and exit.
  -t, --targets=<targets>[,<targets>...]
                  List of targets (http://cluster1,http://cluster2), note you can also use the TARGET_LOCUSTS environment variable.
  -V, --version   Print version information and exit.
```

# Access Locust via proxy

Once started, access your localhost port 8080 instead of locust default port 8089. It will show the first instance of the cluster, but any statistics will actually come from the gathered data from the listed locust servers.

Note: it's not perfect solution, but it help already to get the computed values like the cumulated RPS etc... best ofcourse could be to build a new UI that would be dedicated to work with this tool instead of the locust server one.

## Micronaut 3.8.7 Documentation

- [User Guide](https://docs.micronaut.io/3.8.7/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.8.7/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.8.7/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)


## Feature mockito documentation

- [https://site.mockito.org](https://site.mockito.org)


## Feature openapi documentation

- [Micronaut OpenAPI Support documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html)

- [https://www.openapis.org](https://www.openapis.org)


## Feature awaitility documentation

- [https://github.com/awaitility/awaitility](https://github.com/awaitility/awaitility)


## Feature swagger-ui documentation

- [Micronaut Swagger UI documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html)

- [https://swagger.io/tools/swagger-ui/](https://swagger.io/tools/swagger-ui/)


