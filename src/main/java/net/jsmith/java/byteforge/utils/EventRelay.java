package net.jsmith.java.byteforge.utils;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class EventRelay {

    private final EventBus destinationBus;
    private final Executor destinationExecutor;

    private final Object pendingEventsLock;
    private List< Object > pendingEvents;

    public EventRelay( EventBus destinationBus ) {
        this( destinationBus, MoreExecutors.directExecutor( ) );
    }

    public EventRelay( EventBus destinationBus, Executor destinationExecutor ) {
        this.destinationBus = Objects.requireNonNull( destinationBus, "destinationBus" );
        this.destinationExecutor = Objects.requireNonNull( destinationExecutor, "destinationExecutor" );

        this.pendingEventsLock = new Object( );
        this.pendingEvents = new LinkedList< >( );
    }

    @Subscribe
    private void onEvent( Object event ) {
        boolean schedule;
        synchronized( this.pendingEventsLock ) {
            schedule = this.pendingEvents.isEmpty( );
            this.pendingEvents.add( event );
        }
        if( schedule ) {
            this.destinationExecutor.execute( ( ) -> {
                List< Object > events;
                synchronized( this.pendingEventsLock ) {
                    events = this.pendingEvents;
                    this.pendingEvents = new LinkedList< >( );
                }

                events.forEach( this.destinationBus::post );
            } );
        }
    }

}
