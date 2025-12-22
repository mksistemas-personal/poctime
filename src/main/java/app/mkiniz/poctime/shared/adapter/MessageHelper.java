package app.mkiniz.poctime.shared.adapter;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;

public final class MessageHelper {
    public static <T> Message<T> buildMessage(T payload) {
        return MessageBuilder.withPayload(payload)
                .setHeader("event_type", payload.getClass().getSimpleName())
                .setHeader("source", "poctime-api")
                .build();
    }

    public static <T> Message<T> buildMessage(T payload, Map<String, ?> headers) {
        return MessageBuilder.withPayload(payload)
                .setHeader("event_type", payload.getClass().getSimpleName())
                .setHeader("source", "poctime-api")
                .copyHeaders(headers)
                .build();
    }

    public static <T> Message<T> buildMessage(T payload, String eventType) {
        return MessageBuilder.withPayload(payload)
                .setHeader("event_type", eventType)
                .setHeader("source", "poctime-api")
                .build();
    }

    public static <T> Message<T> buildMessage(T payload, String eventType, Map<String, ?> headers) {
        return MessageBuilder.withPayload(payload)
                .setHeader("event_type", eventType)
                .setHeader("source", "poctime-api")
                .copyHeaders(headers)
                .build();
    }

    public static <T> Message<T> buildMessage(T payload, String eventType, String source) {
        return MessageBuilder.withPayload(payload)
                .setHeader("event_type", eventType)
                .setHeader("source", source)
                .build();
    }

    public static <T> Message<T> buildMessage(T payload, String eventType, String source, Map<String, ?> headers) {
        return MessageBuilder.withPayload(payload)
                .setHeader("event_type", eventType)
                .setHeader("source", source)
                .copyHeaders(headers)
                .build();
    }
}
