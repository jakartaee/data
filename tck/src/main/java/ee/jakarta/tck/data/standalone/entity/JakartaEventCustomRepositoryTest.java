/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.standalone.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.assertj.core.groups.Tuple;

import org.junit.jupiter.api.BeforeEach;

import jakarta.inject.Inject;

import java.util.List;

@Standalone
@AnyEntity
public class JakartaEventCustomRepositoryTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(
                    JakartaEventBuiltInRepositoryTest.class,
                    LifecycleEventType.class,
                    MusicRecordLifecycleObserver.class,
                    MusicRecordRepository.class,
                    MusicRecord.class,
                    MusicStore.class,
                    ObservedEvent.class
                );
    }

    @Inject
    MusicRecordLifecycleObserver observer;

    @Inject
    MusicStore repository;

    @BeforeEach
    void setUp() {
        this.repository.deleteAll();
        this.observer.reset();
        TestPropertyUtility.waitForEventualConsistency();
        TestPropertyUtility.waitForEventualConsistency();
    }

    @Assertion(id = "1530", 
               strategy = "Insert one entity via a custom insert method and verify that pre-insert and post-insert lifecycle events are fired.")
    void shouldFireEventsForOneEntity() {
        // given
        var entity = firstEntity();

        // when
        repository.insert(entity);
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactly(
                        event(LifecycleEventType.PRE_INSERT, entity),
                        event(LifecycleEventType.POST_INSERT, entity));
    }

    @Assertion(id = "1530", 
               strategy = "Insert a list of entities via a custom insert method and verify that pre-insert and post-insert lifecycle events are fired for each entity.")
    void shouldFireEventsForEntityList() {
        // given
        var first = firstEntity();
        var second = secondEntity();
        var entities = List.of(first, second);

        // when
        repository.insert(entities);
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_INSERT, first),
                        event(LifecycleEventType.PRE_INSERT, second),
                        event(LifecycleEventType.POST_INSERT, first),
                        event(LifecycleEventType.POST_INSERT, second));
    }

    @Assertion(id = "1530", 
               strategy = "Insert an array of entities via a custom insert method and verify that pre-insert and post-insert lifecycle events are fired for each entity.")
    void shouldFireEventsForEntityArray() {
        // given
        var first = firstEntity();
        var second = secondEntity();

        // when
        repository.insert(new MusicRecord[]{first, second});
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_INSERT, first),
                        event(LifecycleEventType.PRE_INSERT, second),
                        event(LifecycleEventType.POST_INSERT, first),
                        event(LifecycleEventType.POST_INSERT, second));
    }

    @Assertion(id = "1530", 
               strategy = "Update one entity via a custom update method and verify that pre-update and post-update lifecycle events are fired.")
    void shouldFireUpdateEventsForOneEntity() {
        // given
        var entity = firstEntity();

        repository.insert(entity);
        observer.reset();
        TestPropertyUtility.waitForEventualConsistency();

        // when
        repository.update(entity);
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactly(
                        event(LifecycleEventType.PRE_UPDATE, entity),
                        event(LifecycleEventType.POST_UPDATE, entity));
    }

    @Assertion(id = "1530", 
               strategy = "Update a list of entities via a custom update method and verify that pre-update and post-update lifecycle events are fired for each entity.")
    void shouldFireUpdateEventsForEntityList() {
        // given
        var first = firstEntity();
        var second = secondEntity();
        var entities = List.of(first, second);

        repository.insert(entities);
        observer.reset();
        TestPropertyUtility.waitForEventualConsistency();

        // when
        repository.update(entities);
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_UPDATE, first),
                        event(LifecycleEventType.PRE_UPDATE, second),
                        event(LifecycleEventType.POST_UPDATE, first),
                        event(LifecycleEventType.POST_UPDATE, second));
    }

    @Assertion(id = "1530", 
               strategy = "Update an array of entities via a custom update method and verify that pre-update " + 
                          "and post-update lifecycle events are fired for each entity.")
    void shouldFireUpdateEventsForEntityArray() {
        // given
        var first = firstEntity();
        var second = secondEntity();

        var entities = List.of(first, second);
        repository.insert(entities);
        observer.reset();
        TestPropertyUtility.waitForEventualConsistency();

        // when
        repository.update(new MusicRecord[]{first, second});
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_UPDATE, first),
                        event(LifecycleEventType.PRE_UPDATE, second),
                        event(LifecycleEventType.POST_UPDATE, first),
                        event(LifecycleEventType.POST_UPDATE, second));
    }

    @Assertion(id = "1530", 
               strategy = "Save a new entity via a custom save method and verify that pre-upsert and post-upsert lifecycle events are fired.")
    void shouldFireUpsertEventsForOneNewEntity() {
        // given
        var entity = firstEntity();

        // when
        repository.save(entity);
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactly(
                        event(LifecycleEventType.PRE_UPSERT, entity),
                        event(LifecycleEventType.POST_UPSERT, entity));
    }

    @Assertion(id = "1530", strategy = "Save an existing entity via a custom save method and verify that pre-upsert and post-upsert lifecycle events are fired.")
    void shouldFireUpsertEventsForOneExistingEntity() {
        // given
        var entity = firstEntity();

        repository.insert(entity);
        observer.reset();
        TestPropertyUtility.waitForEventualConsistency();

        // when
        repository.save(entity);
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactly(
                        event(LifecycleEventType.PRE_UPSERT, entity),
                        event(LifecycleEventType.POST_UPSERT, entity));
    }

    @Assertion(id = "1530", 
               strategy = "Save a list of entities via a custom save method and verify that pre-upsert and post-upsert lifecycle events are fired for each entity.")
    void shouldFireUpsertEventsForEntityList() {
        // given
        var first = firstEntity();
        var second = secondEntity();

        // when
        repository.save(List.of(first, second));
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_UPSERT, first),
                        event(LifecycleEventType.POST_UPSERT, first),
                        event(LifecycleEventType.PRE_UPSERT, second),
                        event(LifecycleEventType.POST_UPSERT, second));
    }

    @Assertion(id = "1530", 
               strategy = "Save an array of entities via a custom save method and verify that pre-upsert and post-upsert lifecycle events are fired for each entity.")
    void shouldFireUpsertEventsForEntityArray() {
        // given
        var first = firstEntity();
        var second = secondEntity();

        // when
        repository.save(new MusicRecord[]{first, second});
        TestPropertyUtility.waitForEventualConsistency();

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_UPSERT, first),
                        event(LifecycleEventType.POST_UPSERT, first),
                        event(LifecycleEventType.PRE_UPSERT, second),
                        event(LifecycleEventType.POST_UPSERT, second));
    }

    @Assertion(id = "1530", 
               strategy = "Delete one entity via a custom delete method and verify that pre-delete and post-delete lifecycle events are fired.")
    void shouldFireDeleteEventsForOneEntity() {
        // given
        var entity = firstEntity();
        repository.insert(entity);
        observer.reset();
        TestPropertyUtility.waitForEventualConsistency();

        // when
        repository.delete(entity);

        // then
        assertThat(events())
                .containsExactly(
                        event(LifecycleEventType.PRE_DELETE, entity),
                        event(LifecycleEventType.POST_DELETE, entity));
    }

    @Assertion(id = "1530", 
               strategy = "Delete a list of entities via a custom delete method and verify that pre-delete and post-delete lifecycle events are fired for each entity.")
    void shouldFireDeleteEventsForEntityList() {
        // given
        var first = firstEntity();
        var second = secondEntity();

        repository.insert(first);
        repository.insert(second);
        observer.reset();
        TestPropertyUtility.waitForEventualConsistency();

        // when
        repository.delete(List.of(first, second));

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_DELETE, first),
                        event(LifecycleEventType.PRE_DELETE, second),
                        event(LifecycleEventType.POST_DELETE, first),
                        event(LifecycleEventType.POST_DELETE, second));
    }

    @Assertion(id = "1530", 
               strategy = "Delete an array of entities via a custom delete method and verify that pre-delete and post-delete lifecycle events are fired for each entity.")
    void shouldFireDeleteEventsForEntityArray() {
        // given
        var first = firstEntity();
        var second = secondEntity();
        repository.insert(first);
        repository.insert(second);
        observer.reset();
        TestPropertyUtility.waitForEventualConsistency();

        // when
        repository.delete(new MusicRecord[]{first, second});

        // then
        assertThat(events())
                .containsExactlyInAnyOrder(
                        event(LifecycleEventType.PRE_DELETE, first),
                        event(LifecycleEventType.PRE_DELETE, second),
                        event(LifecycleEventType.POST_DELETE, first),
                        event(LifecycleEventType.POST_DELETE, second));
    }

    private List<Tuple> events() {
        return observer.events().stream()
                .map(this::event)
                .toList();
    }

    private Tuple event(ObservedEvent event) {
        assertThat(event.entity())
                .as("entity for %s", event.type())
                .isInstanceOf(MusicRecord.class);

        return event(event.type(), event.entity());
    }

    private Tuple event(
            LifecycleEventType type,
            MusicRecord entity) {
        return tuple(
                type,
                entity.getCatalogNumber(),
                entity.getTitle(),
                entity.getArtist(),
                entity.getReleaseYear());
    }

    private MusicRecord firstEntity() {
        return new MusicRecord(
                "BLUE-1959",
                "Kind of Blue",
                "Miles Davis",
                1959);
    }

    private MusicRecord secondEntity() {
        return new MusicRecord(
                "IMPULSE-1965",
                "A Love Supreme",
                "John Coltrane",
                1965);
    }
}
