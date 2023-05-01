/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.read.only;

import java.io.Serializable;

@jakarta.nosql.Entity
@jakarta.persistence.Entity
public class AsciiCharacter implements Serializable {
    private static final long serialVersionUID = 1L;

    @jakarta.nosql.Id
    @jakarta.persistence.Id
    private long id;

    @jakarta.nosql.Column
    private int decimal;

    @jakarta.nosql.Column
    private String hexadecimal;

    @jakarta.nosql.Column
    private char thisCharacter;

    @jakarta.nosql.Column
    private boolean isControl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public String getHexadecimal() {
        return hexadecimal;
    }

    public void setHexadecimal(String hexadecimal) {
        this.hexadecimal = hexadecimal;
    }

    public char getThisCharacter() {
        return thisCharacter;
    }

    public void setThisCharacter(char thisCharacter) {
        this.thisCharacter = thisCharacter;
    }

    public boolean isControl() {
        return isControl;
    }

    public void setControl(boolean isControl) {
        this.isControl = isControl;
    }

}
