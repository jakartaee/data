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

import jakarta.data.metamodel.AttributeMetadata;
import jakarta.data.metamodel.StaticMetamodel;

/**
 * This static metamodel class represents what a user might explicitly provide,
 * in which case the Jakarta Data provider will need to initialize the attributes.
 */
@StaticMetamodel(AsciiCharacter.class)
public interface AsciiChar_ {
    AttributeMetadata id = AttributeMetadata.get();
    AttributeMetadata hexadecimal = AttributeMetadata.get();
    AttributeMetadata isControl = AttributeMetadata.get();
    AttributeMetadata numericValue = AttributeMetadata.get();
    AttributeMetadata thisCharacter = AttributeMetadata.get();
}
