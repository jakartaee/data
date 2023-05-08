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

import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;

public class NaturalNumbersPopulator implements Populator<NaturalNumbers> {
    private static final Logger log = Logger.getLogger(NaturalNumbersPopulator.class.getCanonicalName());
    
    public static NaturalNumbersPopulator get() {
        return new NaturalNumbersPopulator();
    }
    
    @Override
    public void populate(NaturalNumbers repo) {
        log.info("NaturalNumbers creating set");
        List<NaturalNumber> dictonary = new ArrayList<>();
        
        IntStream.range(1, 101)
            .forEach(id -> {
                NaturalNumber inst = new NaturalNumber();
                
                boolean isOne = id == 1;
                boolean isOdd = id % 2 == 1;
                long sqrRoot = squareRoot(id);
                boolean isPrime = isOdd ? isPrime(id, sqrRoot) : (id == 2);
                
                inst.setNumericValue(id);
                inst.setOdd(isOdd);
                inst.setNumBitsRequired(bitsRequired(id));
                inst.setNumType(isOne ? NumberType.ONE : isPrime ? NumberType.PRIME : NumberType.COMPOSITE);
                inst.setFloorOfSquareRoot(sqrRoot);
                
                dictonary.add(inst);
            });
        
        repo.saveAll(dictonary);
        log.info("NaturalNumbers populated");
    }
    
    @Override
    public boolean isPopulated(NaturalNumbers repo) {
       return repo.count() == 100;
    }
    
    private static Short bitsRequired(int value) {
        return (short) (Math.floor(Math.log(value) / Math.log(2)) + 1);
    }
    
    private static long squareRoot(int value) {
        return (long) Math.floor(Math.sqrt(value));
    }
    
    private static boolean isPrime(int value, long largestPossibleFactor) {
        if(value == 1)
            return false;
        
        for(int i = 2; i <= largestPossibleFactor; i++) {
            if( value % i == 0 )
                return false;
        }
        return true;
    }
}
