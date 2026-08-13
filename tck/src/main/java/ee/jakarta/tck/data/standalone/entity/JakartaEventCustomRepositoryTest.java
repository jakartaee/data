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

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import org.jboss.arquillian.container.test.api.Deployment;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import jakarta.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import java.util.List;

@Standalone
@AnyEntity
@DisplayName("Custom repository lifecycle events")
public class JakartaEventCustomRepositoryTest {

    public static final Logger log = Logger.getLogger(JakartaEventCustomRepositoryTest.class.getCanonicalName());

    protected final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(JakartaEventCustomRepositoryTest.class);
    }

    @Inject
    private MusicRecordLifecycleObserver observer;

    @Inject
    private MusicStore repository;

    @BeforeEach
    void setUp() {
        this.repository.deleteAll();
        this.observer.reset();
    }

    @Nested
    @DisplayName("When inserting entities")
    class WhenInsert {

        @Test
        @DisplayName("Should fire pre-insert and post-insert events when inserting one entity")
        void shouldFireEventsForOneEntity() {
            // given
            var entity = firstEntity();

            // when
            repository.insert(entity);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_INSERT, entity),
                            event(LifecycleEventType.POST_INSERT, entity));
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("Should fire pre-insert and post-insert events for each entity in a list")
        void shouldFireEventsForEntityList() {
            // given
            var first = firstEntity();
            var second = secondEntity();
            var entities = List.of(first, second);

            // when
            repository.insert(entities);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_INSERT, first),
                            event(LifecycleEventType.PRE_INSERT, second),
                            event(LifecycleEventType.POST_INSERT, first),
                            event(LifecycleEventType.POST_INSERT, second));
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("Should fire pre-insert and post-insert events for each entity in an array")
        void shouldFireEventsForEntityArray() {
            // given
            var first = firstEntity();
            var second = secondEntity();

            // when
            repository.insert(new MusicRecord[]{first, second});

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_INSERT, first),
                            event(LifecycleEventType.PRE_INSERT, second),
                            event(LifecycleEventType.POST_INSERT, first),
                            event(LifecycleEventType.POST_INSERT, second));
        }
    }

    @Nested
    @DisplayName("When updating entities")
    class WhenUpdate {

        @Test
        @DisplayName("Should fire pre-update and post-update events when updating one entity")
        void shouldFireEventsForOneEntity() {
            // given
            var entity = firstEntity();

            repository.insert(entity);
            observer.reset();
            // when
            repository.update(entity);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_UPDATE, entity),
                            event(LifecycleEventType.POST_UPDATE, entity));
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("Should fire pre-update and post-update events for each entity in a list")
        void shouldFireEventsForEntityList() {
            // given
            var first = firstEntity();
            var second = secondEntity();
            var entities = List.of(first, second);

            repository.insert(entities);
            observer.reset();

            // when
            repository.update(entities);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_UPDATE, first),
                            event(LifecycleEventType.PRE_UPDATE, second),
                            event(LifecycleEventType.POST_UPDATE, first),
                            event(LifecycleEventType.POST_UPDATE, second));
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("Should fire pre-update and post-update events for each entity in an array")
        void shouldFireEventsForEntityArray() {
            // given
            var first = firstEntity();
            var second = secondEntity();

            var entities = List.of(first, second);
            repository.insert(entities);
            observer.reset();

            // when
            repository.update(new MusicRecord[]{first, second});

            // then
            assertThat(events())
                    .containsExactlyInAnyOrder(
                            event(LifecycleEventType.PRE_UPDATE, first),
                            event(LifecycleEventType.PRE_UPDATE, second),
                            event(LifecycleEventType.POST_UPDATE, first),
                            event(LifecycleEventType.POST_UPDATE, second));
        }
    }

    @Nested
    @DisplayName("When saving entities")
    class WhenSave {

        @Test
        @DisplayName("Should fire pre-upsert and post-upsert events when saving one new entity")
        void shouldFireEventsForOneNewEntity() {
            // given
            var entity = firstEntity();

            // when
            repository.save(entity);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_UPSERT, entity),
                            event(LifecycleEventType.POST_UPSERT, entity));
        }

        @Test
        @DisplayName("Should fire pre-upsert and post-upsert events when saving one existing entity")
        void shouldFireEventsForOneExistingEntity() {
            // given
            var entity = firstEntity();

            repository.insert(entity);
            observer.reset();


            // when
            repository.save(entity);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_UPSERT, entity),
                            event(LifecycleEventType.POST_UPSERT, entity));
        }

        @Test
        @DisplayName("Should fire pre-upsert and post-upsert events for each entity in a list")
        void shouldFireEventsForEntityList() {
            // given
            var first = firstEntity();
            var second = secondEntity();

            // when
            repository.save(List.of(first, second));

            // then
            assertThat(events())
                    .containsExactlyInAnyOrder(
                            event(LifecycleEventType.PRE_UPSERT, first),
                            event(LifecycleEventType.POST_UPSERT, first),
                            event(LifecycleEventType.PRE_UPSERT, second),
                            event(LifecycleEventType.POST_UPSERT, second));
        }

        @Test
        @DisplayName("Should fire pre-upsert and post-upsert events for each entity in an array")
        void shouldFireEventsForEntityArray() {
            // given
            var first = firstEntity();
            var second = secondEntity();

            // when
            repository.save(new MusicRecord[]{first, second});

            // then
            assertThat(events())
                    .containsExactlyInAnyOrder(
                            event(LifecycleEventType.PRE_UPSERT, first),
                            event(LifecycleEventType.POST_UPSERT, first),
                            event(LifecycleEventType.PRE_UPSERT, second),
                            event(LifecycleEventType.POST_UPSERT, second));
        }
    }

    @Nested
    @DisplayName("When deleting entities")
    class WhenDelete {

        @Test
        @DisplayName("Should fire pre-delete and post-delete events when deleting one entity")
        void shouldFireEventsForOneEntity() {
            // given
            var entity = firstEntity();

            // when
            repository.delete(entity);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_DELETE, entity),
                            event(LifecycleEventType.POST_DELETE, entity));
        }

        @Test
        @DisplayName("Should fire pre-delete and post-delete events for each entity in a list")
        void shouldFireEventsForEntityList() {
            // given
            var first = firstEntity();
            var second = secondEntity();

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

        @Test
        @DisplayName("Should fire pre-delete and post-delete events for each entity in an array")
        void shouldFireEventsForEntityArray() {
            // given
            var first = firstEntity();
            var second = secondEntity();

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