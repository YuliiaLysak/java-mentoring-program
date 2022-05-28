package edu.lysak.sport.setup;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.service.SportService;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriberWithBackpressure implements Subscriber<Sport> {
    private static final int LIMIT = 20;
    private Subscription subscription;
    private int onNextAmount;

    private final SportService sportService;

    public SubscriberWithBackpressure(SportService sportService) {
        this.sportService = sportService;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        log.info("Sport initialization started");
        subscription.request(LIMIT);
    }

    @Override
    public void onNext(Sport sport) {
        if (sport != null) {
            sportService.save(sport).subscribe();
        }
        onNextAmount++;
        if (onNextAmount % LIMIT == 0) {
            subscription.request(LIMIT);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (throwable instanceof DataIntegrityViolationException) {
            log.error("Error: %s", throwable);
        }
        onNextAmount++;
        if (onNextAmount % LIMIT == 0) {
            subscription.request(LIMIT);
        }
    }

    @Override
    public void onComplete() {
        log.info("Sport initialization completed");
    }
}
