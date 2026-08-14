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
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;
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
@DisplayName("Built-in repository lifecycle events")
public class JakartaEventBuiltInRepositoryTest {

    public static final Logger log = Logger.getLogger(JakartaEventBuiltInRepositoryTest.class.getCanonicalName());

    protected final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(JakartaEventBuiltInRepositoryTest.class);
    }

    @Inject
    protected MusicRecordLifecycleObserver observer;

    @Inject
    protected MusicRecordRepository repository;

    @BeforeEach
    void setUp() {
        this.repository.deleteAll();
        this.observer.reset();
        TestPropertyUtility.waitForEventualConsistency();
    }

    @Nested
    @DisplayName("When inserting an entity")
    class WhenInsert {

        @Test
        @DisplayName("Should fire pre-insert and post-insert events with the inserted entity")
        void shouldFireInsertEvents() {
            // given
            MusicRecord entity = entity();

            // when
            repository.insert(entity);
            TestPropertyUtility.waitForEventualConsistency();

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_INSERT, entity),
                            event(LifecycleEventType.POST_INSERT, entity));
        }
    }

    @Nested
    @DisplayName("When updating an entity")
    class WhenUpdate {

        @Test
        @DisplayName("Should fire pre-update and post-update events with the updated entity")
        void shouldFireUpdateEvents() {
            // given
            MusicRecord entity = entity();
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
    }

    @Nested
    @DisplayName("When saving a new entity")
    class WhenSaveNewEntity {

        @Test
        @DisplayName("Should fire pre-upsert and post-upsert events when inserting a missing entity")
        void shouldFireUpsertEventsWhenInserting() {
            // given
            MusicRecord entity = entity();

            // when
            repository.save(entity);
            TestPropertyUtility.waitForEventualConsistency();

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_UPSERT, entity),
                            event(LifecycleEventType.POST_UPSERT, entity));
        }
    }

    @Nested
    @DisplayName("When saving an existing entity")
    class WhenSaveExistingEntity {

        @Test
        @DisplayName("Should fire pre-upsert and post-upsert events when updating an existing entity")
        void shouldFireUpsertEventsWhenUpdating() {
            // given
            MusicRecord entity = entity();

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
    }

    @Nested
    @DisplayName("When deleting an entity")
    class WhenDelete {

        @Test
        @DisplayName("Should fire pre-delete and post-delete events with the deleted entity")
        void shouldFireDeleteEvents() {
            // given
            MusicRecord entity = entity();

            // when
            repository.delete(entity);

            // then
            assertThat(events())
                    .containsExactly(
                            event(LifecycleEventType.PRE_DELETE, entity),
                            event(LifecycleEventType.POST_DELETE, entity));
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

    private MusicRecord entity() {
        return new MusicRecord(
                "BLUE-1959",
                "Kind of Blue",
                "Miles Davis",
                1959);
    }
}
