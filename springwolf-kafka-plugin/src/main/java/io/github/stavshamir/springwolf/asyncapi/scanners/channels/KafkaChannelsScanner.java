package io.github.stavshamir.springwolf.asyncapi.scanners.channels;

import com.google.common.collect.ImmutableMap;
import io.github.stavshamir.springwolf.asyncapi.scanners.components.ComponentsScanner;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.bindings.OperationBinding;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.bindings.kafka.KafkaOperationBinding;
import io.github.stavshamir.springwolf.configuration.KafkaProtocolConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringValueResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaChannelsScanner extends AbstractChannelScanner<KafkaListener>
        implements ChannelsScanner, EmbeddedValueResolverAware {

    private final KafkaProtocolConfiguration kafkaProtocolConfiguration;
    private final ComponentsScanner componentsScanner;

    private StringValueResolver resolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected Set<Class<?>> getClassesToScan() {
        String basePackage = kafkaProtocolConfiguration.getBasePackage();
        return componentsScanner.scanForComponents(basePackage);
    }

    @Override
    protected Class<KafkaListener> getListenerAnnotationClass() {
        return KafkaListener.class;
    }

    @Override
    protected String getChannelName(KafkaListener annotation) {
        List<String> resolvedTopics = Arrays.stream(annotation.topics())
                .map(resolver::resolveStringValue)
                .collect(toList());

        log.debug("Found topics: {}", String.join(", ", resolvedTopics));
        return resolvedTopics.get(0);
    }

    @Override
    protected Map<String, ? extends OperationBinding> buildOperationBinding(KafkaListener annotation) {
        String groupId = annotation.groupId();

        if (groupId.isEmpty()) {
            log.debug("No group ID found for this listener");
        } else {
            log.debug("Found group id: {}", groupId);
        }

        KafkaOperationBinding binding = KafkaOperationBinding.withGroupId(groupId);
        return ImmutableMap.of(KafkaOperationBinding.KAFKA_BINDING_KEY, binding);
    }
}
