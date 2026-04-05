package iu.devinmehringer.project2.controller;

import iu.devinmehringer.project2.model.order.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/badge")
public class BadgeController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void pushBadgeUpdate(Order order, String event, String actor) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().data(event));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        });
    }
}