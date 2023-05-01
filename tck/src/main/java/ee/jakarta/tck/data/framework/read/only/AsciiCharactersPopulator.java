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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import jakarta.data.repository.CrudRepository;

public class AsciiCharactersPopulator implements Populator {
    private static final Logger log = Logger.getLogger(AsciiCharactersPopulator.class.getCanonicalName());
    
    private AsciiCharacters repo;
    
    public AsciiCharactersPopulator(CrudRepository<?,Long> repo) {
        this.repo = (AsciiCharacters) repo;
    }

    @Override
    public void populate() {
        log.info("AsciiCharacters creating set");
        List<AsciiCharacter> dictonary = new ArrayList<>();
        
        IntStream.range(0, 127)
            .forEach(decimal -> {
                AsciiCharacter inst = new AsciiCharacter();
                
                inst.setId(decimal + 1); //Some JPA providers may not support id's of 0
                inst.setDecimal(decimal);
                inst.setHexadecimal(Integer.toHexString(decimal));
                inst.setThisCharacter((char) decimal);
                inst.setControl(Character.isISOControl((char) decimal));
                
                dictonary.add(inst);
            });
        
        repo.saveAll(dictonary);
        log.info("AsciiCharacters populated");
    }
}
