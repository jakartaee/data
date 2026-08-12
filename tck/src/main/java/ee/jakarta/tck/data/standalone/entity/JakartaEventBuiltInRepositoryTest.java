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
    private MusicRecordRepository repository;


    @Nested
    @DisplayName("When inserting an entity")
    class WhenInsert {

        @Test
        @DisplayName("Should fire pre-insert and post-insert events with the inserted entity")
        void shouldFireInsertEvents() {
            // given
            MusicRecord entity = entity();

            when(template.insert(entity))
                    .thenReturn(entity);

            // when
            MusicRecord result = repository.insert(entity);

            // then
            assertThat(result).isSameAs(entity);

            assertThat(observer.events())
                    .containsExactly(
                            new ObservedEvent(
                                    LifecycleEventType.PRE_INSERT,
                                    entity),
                            new ObservedEvent(
                                    LifecycleEventType.POST_INSERT,
                                    result));
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

            when(template.update(entity))
                    .thenReturn(entity);

            // when
            MusicRecord result = repository.update(entity);

            // then
            assertThat(result).isSameAs(entity);

            assertThat(observer.events())
                    .containsExactly(
                            new ObservedEvent(
                                    LifecycleEventType.PRE_UPDATE,
                                    entity),
                            new ObservedEvent(
                                    LifecycleEventType.POST_UPDATE,
                                    result));
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

            when(template.find(
                    MusicRecord.class,
                    entity.catalogNumber()))
                    .thenReturn(Optional.empty());

            when(template.insert(entity))
                    .thenReturn(entity);

            // when
            MusicRecord result = repository.save(entity);

            // then
            assertThat(result).isSameAs(entity);

            assertThat(observer.events())
                    .containsExactly(
                            new ObservedEvent(
                                    LifecycleEventType.PRE_UPSERT,
                                    entity),
                            new ObservedEvent(
                                    LifecycleEventType.POST_UPSERT,
                                    result));
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

            when(template.find(MusicRecord.class, entity.catalogNumber())).thenReturn(Optional.of(entity));

            when(template.update(entity)).thenReturn(entity);

            // when
            MusicRecord result = repository.save(entity);

            // then
            assertThat(result).isSameAs(entity);

            assertThat(observer.events())
                    .containsExactly(
                            new ObservedEvent(
                                    LifecycleEventType.PRE_UPSERT,
                                    entity),
                            new ObservedEvent(
                                    LifecycleEventType.POST_UPSERT,
                                    result));
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
            assertThat(observer.events())
                    .containsExactly(
                            new ObservedEvent(
                                    LifecycleEventType.PRE_DELETE,
                                    entity),
                            new ObservedEvent(
                                    LifecycleEventType.POST_DELETE,
                                    entity));
        }
    }

    private MusicRecord entity() {
        return new MusicRecord(
                "BLUE-1959",
                "Kind of Blue",
                "Miles Davis",
                1959);
    }
}
