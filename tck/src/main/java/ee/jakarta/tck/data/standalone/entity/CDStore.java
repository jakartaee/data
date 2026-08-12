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

import jakarta.data.repository.Delete;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;

import java.util.List;

@Repository
public interface CDStore {


    @Insert
    void insert(MusicRecord musicRecord);
    @Insert
    void insert(List<MusicRecord> musicRecords);
    @Insert
    void insert(MusicRecord[] musicRecords);

    @Update
    void update(MusicRecord musicRecord);
    @Update
    void update(List<MusicRecord> musicRecords);
    @Update
    void update(MusicRecord[] vinylRecords);

    @Save
    void save(MusicRecord musicRecord);
    @Save
    void save(List<MusicRecord> musicRecords);
    @Save
    void save(MusicRecord[] musicRecords);

    @Delete
    void delete(MusicRecord musicRecord);
    @Delete
    void delete(List<MusicRecord> musicRecords);
    @Delete
    void delete(MusicRecord[] musicRecords);
}
