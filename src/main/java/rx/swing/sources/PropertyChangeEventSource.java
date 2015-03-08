package rx.swing.sources;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.observables.SwingObservable;
import rx.subscriptions.SwingSubscriptions;

public enum PropertyChangeEventSource { ; // no instances

    public static Observable<PropertyChangeEvent> fromPropertyChangeEventsOf(final Component component) {
        return Observable.create(new OnSubscribe<PropertyChangeEvent>() {
            @Override
            public void call(final Subscriber<? super PropertyChangeEvent> subscriber) {
                SwingObservable.assertEventDispatchThread();
                final PropertyChangeListener listener = new PropertyChangeListener()
                {
                    @Override
                    public void propertyChange(PropertyChangeEvent event)
                    {
                        subscriber.onNext(event);
                    }
                };
                component.addPropertyChangeListener(listener);
                subscriber.add(SwingSubscriptions.unsubscribeInEventDispatchThread(new Action0() {
                    @Override
                    public void call() {
                        component.removePropertyChangeListener(listener);
                    }
                }));
            }
        });
    }
}
