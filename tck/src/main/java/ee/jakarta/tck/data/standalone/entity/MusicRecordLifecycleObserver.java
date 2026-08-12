/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 * <p>
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.standalone.entity;

import jakarta.data.event.PostDeleteEvent;
import jakarta.data.event.PostInsertEvent;
import jakarta.data.event.PostUpdateEvent;
import jakarta.data.event.PostUpsertEvent;
import jakarta.data.event.PreDeleteEvent;
import jakarta.data.event.PreInsertEvent;
import jakarta.data.event.PreUpdateEvent;
import jakarta.data.event.PreUpsertEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.util.ArrayList;
import java.util.List;

 @ApplicationScoped
 public class MusicRecordLifecycleObserver {

    private final List<ObservedEvent> events = new ArrayList<>();

    void onPreInsert(@Observes PreInsertEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.PRE_INSERT,
                event.entity()));
    }

    void onPostInsert(@Observes PostInsertEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.POST_INSERT,
                event.entity()));
    }

    void onPreUpdate(@Observes PreUpdateEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.PRE_UPDATE,
                event.entity()));
    }

    void onPostUpdate(@Observes PostUpdateEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.POST_UPDATE,
                event.entity()));
    }

    void onPreUpsert(@Observes PreUpsertEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.PRE_UPSERT,
                event.entity()));
    }

    void onPostUpsert(@Observes PostUpsertEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.POST_UPSERT,
                event.entity()));
    }

    void onPreDelete(@Observes PreDeleteEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.PRE_DELETE,
                event.entity()));
    }

    void onPostDelete(@Observes PostDeleteEvent<MusicRecord> event) {
        events.add(new ObservedEvent(
                LifecycleEventType.POST_DELETE,
                event.entity()));
    }

    List<ObservedEvent> events() {
        return List.copyOf(events);
    }

    void reset() {
        events.clear();
    }
}