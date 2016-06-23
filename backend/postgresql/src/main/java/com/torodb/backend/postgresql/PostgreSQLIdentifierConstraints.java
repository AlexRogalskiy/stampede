/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */

package com.torodb.backend.postgresql;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableSet;
import com.torodb.backend.AbstractIdentifierConstraints;

/**
 *
 */
@Singleton
public class PostgreSQLIdentifierConstraints extends AbstractIdentifierConstraints {
    
    public PostgreSQLIdentifierConstraints() {
        super(
            ImmutableSet.<String>of(
                    "pg_catalog",
                    "information_schema",
                    "pg_toast",
                    "public" 
                ),
            ImmutableSet.<String>of(
                    "oid",
                    "tableoid",
                    "xmin",
                    "xmax",
                    "cmin",
                    "cmax",
                    "ctid"
                ));
    }

    @Override
    public int identifierMaxSize() {
        return 63;
    }
}
