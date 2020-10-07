![version](https://img.shields.io/github/v/release/springwolf/springwolf-kafka)
![springwolf-core](https://github.com/springwolf/springwolf-kafka/workflows/springwolf-kafka/badge.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Springwolf Kafka Plugin
##### Automated documentation for Spring Boot application with Kafka consumers

### Table Of Contents
- [About](#about)
- [Usage](#usage)
  - [Dependencies](#dependencies)
  - [Configuration class](#configuration-class)
- [Verify](#verify)
- [Example Project](#example-project)

### About
This plugin generates an [AsyncAPI document](https://www.asyncapi.com/) from `@KafkaListener` methods.
For more information, check out [springwolf-core](https://github.com/springwolf/springwolf-core).

### Usage
Add the following dependencies and configuration class to enable this plugin.

#### Dependencies
```groovy
repositories {
    jcenter()
}
dependencies {
    // Provides the documentation API    
    implementation 'io.github.stavshamir:springwolf-kafka:0.1.0'
    
    // Provides the UI - optional (recommended)
    runtimeOnly 'io.github.stavshamir:springwolf-ui:0.0.2'
}
```

### Configuration class
Add a configuration class and provide a `KafkaProtocolConfiguration` bean and a `AsyncApiDocket` bean:
```java
@Configuration
@EnableAsyncApi
public class AsyncApiConfiguration {

    private final static String BOOTSTRAP_SERVERS = "localhost:9092"; // Change to your actual bootstrap server

    @Bean
    public KafkaProtocolConfiguration kafkaProtocolConfiguration() {
        return KafkaProtocolConfiguration.builder()
                .basePackage("io.github.stavshamir.springwolf.example.consumers") // Change to your actual base package of listeners
                .producerConfiguration(buildProducerConfiguration(BOOTSTRAP_SERVERS))
                .build();
    }

    @Bean
    public AsyncApiDocket asyncApiDocket() {
        Info info = Info.builder()
                .version("1.0.0")
                .title("Springwolf example project")
                .build();

        return AsyncApiDocket.builder()
                .info(info)
                .server("kafka", Server.kafka().url(BOOTSTRAP_SERVERS).build())
                .build();
    }

    private Map<String, Object> buildProducerConfiguration(String bootstrapServers) {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
                .put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false)
                .build();
    }

}
```
The basePackage field must be set with the name of the package containing the classes to be scanned for `@KafkaListener`
annotated methods.

#### Verify
If you have included the UI dependency, access it with the following url: `localhost:8080/asyncapi-ui.html`.
If not, try the following endpoint: `localhost:8080/asyncapi/docs`.


### Example Project
See [springwolf-kafka-example](https://github.com/springwolf/springwolf-kafka/tree/master/springwolf-kafka-example).
