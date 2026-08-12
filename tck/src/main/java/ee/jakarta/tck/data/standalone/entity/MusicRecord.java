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

import java.util.Objects;

@jakarta.nosql.Entity
@jakarta.persistence.Entity
public class MusicRecord {

    @jakarta.persistence.Id
    @jakarta.nosql.Id
    private String catalogNumber;

    @jakarta.persistence.Column
    @jakarta.nosql.Column
    private String title;

    @jakarta.persistence.Column
    @jakarta.nosql.Column
    private String artist;

    @jakarta.persistence.Column
    @jakarta.nosql.Column
    private Integer releaseYear;

    public MusicRecord(String catalogNumber, String title, String artist, Integer releaseYear) {
        this.catalogNumber = catalogNumber;
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
    }

    public MusicRecord() {
    }


    public String getCatalogNumber() {
        return catalogNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MusicRecord that)) {
            return false;
        }
        return Objects.equals(catalogNumber, that.catalogNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(catalogNumber);
    }

    @Override
    public String toString() {
        return "MusicRecord{" +
                "catalogNumber='" + catalogNumber + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", releaseYear=" + releaseYear +
                '}';
    }
}
