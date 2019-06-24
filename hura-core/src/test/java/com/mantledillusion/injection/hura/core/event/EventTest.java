package com.mantledillusion.injection.hura.core.event;

import com.mantledillusion.injection.hura.core.*;
import com.mantledillusion.injection.hura.core.event.injectables.InjectableWithBusAndSubscription;
import com.mantledillusion.injection.hura.core.event.injectables.InjectableWithExceptionThrowingSubscription;
import com.mantledillusion.injection.hura.core.event.injectables.InjectableWithMultipleSpecificTypesSubscription;
import com.mantledillusion.injection.hura.core.event.injectables.InjectableWithSpecificTypeSubscription;
import com.mantledillusion.injection.hura.core.event.unijectables.UninjectableWithIncompatibleTypesSubscription;
import com.mantledillusion.injection.hura.core.event.unijectables.UninjectableWithMultipleParameterSubscription;
import com.mantledillusion.injection.hura.core.event.unijectables.UninjectableWithoutAnyTypeSubscription;
import com.mantledillusion.injection.hura.core.exception.EventException;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventTest extends AbstractInjectionTest {

    @Test
    public void testPublishAndReceive() {
        InjectableWithBusAndSubscription injectable = this.suite.injectInRootContext(InjectableWithBusAndSubscription.class);

        Assertions.assertNotNull(injectable.bus);

        Object event = new Object();
        injectable.bus.publish(event);

        Assertions.assertEquals(1, injectable.events.size());
        Assertions.assertSame(event, injectable.events.get(0));
    }

    @Test
    public void testPublishAndReceiveOverSequencesWithoutBusIsolation() {
        InjectableWithInjector injectable = this.suite.injectInRootContext(InjectableWithInjector.class,
                Blueprint.PropertyAllocation.of(Bus.PROPERTY_BUS_ISOLATION, Boolean.FALSE.toString()));

        InjectableWithBusAndSubscription a = injectable.injector.instantiate(InjectableWithBusAndSubscription.class);
        InjectableWithBusAndSubscription b = injectable.injector.instantiate(InjectableWithBusAndSubscription.class);

        Object ea = new Object();
        a.bus.publish(ea);

        Assertions.assertEquals(1, a.events.size());
        Assertions.assertSame(ea, a.events.get(0));
        Assertions.assertEquals(1, b.events.size());
        Assertions.assertSame(ea, b.events.get(0));

        Object eb = new Object();
        b.bus.publish(eb);

        Assertions.assertEquals(2, a.events.size());
        Assertions.assertSame(eb, a.events.get(1));
        Assertions.assertEquals(2, b.events.size());
        Assertions.assertSame(eb, b.events.get(1));
    }

    @Test
    public void testPublishAndReceiveOverSequencesWithBusIsolation() {
        InjectableWithInjector injectable = this.suite.injectInRootContext(InjectableWithInjector.class,
                Blueprint.PropertyAllocation.of(Bus.PROPERTY_BUS_ISOLATION, Boolean.TRUE.toString()));

        InjectableWithBusAndSubscription a = injectable.injector.instantiate(InjectableWithBusAndSubscription.class);
        InjectableWithBusAndSubscription b = injectable.injector.instantiate(InjectableWithBusAndSubscription.class);

        Object ea = new Object();
        a.bus.publish(ea);

        Assertions.assertEquals(1, a.events.size());
        Assertions.assertSame(ea, a.events.get(0));
        Assertions.assertEquals(0, b.events.size());

        Object eb = new Object();
        b.bus.publish(eb);

        Assertions.assertEquals(1, a.events.size());
        Assertions.assertEquals(1, b.events.size());
        Assertions.assertSame(eb, b.events.get(0));
    }

    @Test
    public void testPublishAndReceiveOverSequencesWithEventIsolation() {
        InjectableWithInjector injectable = this.suite.injectInRootContext(InjectableWithInjector.class,
                Blueprint.PropertyAllocation.of(Bus.PROPERTY_BUS_ISOLATION, Boolean.FALSE.toString()));

        InjectableWithBusAndSubscription a = injectable.injector.instantiate(InjectableWithBusAndSubscription.class);
        InjectableWithBusAndSubscription b = injectable.injector.instantiate(InjectableWithBusAndSubscription.class);

        Object ea = new Object();
        a.bus.publish(ea, true);

        Assertions.assertEquals(1, a.events.size());
        Assertions.assertSame(ea, a.events.get(0));
        Assertions.assertEquals(0, b.events.size());

        Object eb = new Object();
        b.bus.publish(eb, true);

        Assertions.assertEquals(1, a.events.size());
        Assertions.assertEquals(1, b.events.size());
        Assertions.assertSame(eb, b.events.get(0));
    }

    @Test
    public void testSpecificTypeSubscription() {
        Injector.RootInjector injector = Injector.of();

        InjectableWithSpecificTypeSubscription injectable = injector.instantiate(InjectableWithSpecificTypeSubscription.class);
        Bus bus = injector.instantiate(Bus.class);

        bus.publish(new Object());

        Assertions.assertEquals(0, injectable.events.size());

        InjectableWithSpecificTypeSubscription.Event event = new InjectableWithSpecificTypeSubscription.Event();
        bus.publish(event);

        Assertions.assertEquals(1, injectable.events.size());
        Assertions.assertSame(event, injectable.events.get(0));
    }

    @Test
    public void testMultipleSpecificTypeSubscription() {
        Injector.RootInjector injector = Injector.of();

        InjectableWithMultipleSpecificTypesSubscription injectable = injector.instantiate(InjectableWithMultipleSpecificTypesSubscription.class);
        Bus bus = injector.instantiate(Bus.class);

        InjectableWithMultipleSpecificTypesSubscription.EventA ea = new InjectableWithMultipleSpecificTypesSubscription.EventA();
        bus.publish(ea);

        Assertions.assertEquals(1, injectable.events.size());
        Assertions.assertSame(ea, injectable.events.get(0));

        InjectableWithMultipleSpecificTypesSubscription.EventB eb = new InjectableWithMultipleSpecificTypesSubscription.EventB();
        bus.publish(eb);

        Assertions.assertEquals(1, injectable.events.size());

        InjectableWithMultipleSpecificTypesSubscription.EventC ec = new InjectableWithMultipleSpecificTypesSubscription.EventC();
        bus.publish(ec);

        Assertions.assertEquals(2, injectable.events.size());
        Assertions.assertSame(ec, injectable.events.get(1));
    }

    @Test
    public void testNoEventPublishing() {
        Bus bus = this.suite.injectInRootContext(Bus.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> bus.publish(null));
    }

    @Test
    public void testNoTypeSubscription() {
        Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInRootContext(UninjectableWithoutAnyTypeSubscription.class));
    }

    @Test
    public void testMultipleParameterSubscription() {
        Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInRootContext(UninjectableWithMultipleParameterSubscription.class));
    }

    @Test
    public void testIncompatibleTypeSubscription() {
        Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInRootContext(UninjectableWithIncompatibleTypesSubscription.class));
    }

    @Test
    public void testExceptionDuringReceival() {
        Injector.RootInjector injector = Injector.of();

        Bus bus = injector.instantiate(Bus.class);
        injector.instantiate(InjectableWithExceptionThrowingSubscription.class);

        Assertions.assertThrows(EventException.class, () -> bus.publish(new Object()));
    }
}
