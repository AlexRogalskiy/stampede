/*
 * ToroDB - ToroDB-poc: Backend common
 * Copyright © 2014 8Kdata Technology (www.8kdata.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.torodb.backend;

import com.google.common.collect.Lists;
import com.torodb.backend.tables.*;
import com.torodb.backend.tables.records.*;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.jooq.DSLContext;

import com.torodb.core.TableRef;
import com.torodb.core.backend.MetaInfoKey;
import com.torodb.core.transaction.metainf.MetaCollection;
import com.torodb.core.transaction.metainf.MetaDatabase;
import com.torodb.core.transaction.metainf.MetaDocPart;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MetaDataReadInterface {
    @Nonnull <R extends MetaDatabaseRecord> MetaDatabaseTable<R> getMetaDatabaseTable();
    @Nonnull <R extends MetaCollectionRecord> MetaCollectionTable<R> getMetaCollectionTable();
    @Nonnull <T, R extends MetaDocPartRecord<T>> MetaDocPartTable<T, R> getMetaDocPartTable();
    @Nonnull <T, R extends MetaFieldRecord<T>> MetaFieldTable<T, R> getMetaFieldTable();
    @Nonnull <T, R extends MetaScalarRecord<T>> MetaScalarTable<T, R> getMetaScalarTable();
    @Nonnull <T, R extends MetaDocPartIndexRecord<T>> MetaDocPartIndexTable<T, R> getMetaDocPartIndexTable();
    @Nonnull <T, R extends MetaDocPartIndexColumnRecord<T>> MetaDocPartIndexColumnTable<T, R> getMetaDocPartIndexColumnTable();
    @Nonnull <R extends MetaIndexRecord> MetaIndexTable<R> getMetaIndexTable();
    @Nonnull <T, R extends MetaIndexFieldRecord<T>> MetaIndexFieldTable<T, R> getMetaIndexFieldTable();
    @Nonnull <R extends KvRecord> KvTable<R> getKvTable();
    
    @Nonnull Collection<InternalField<?>> getInternalFields(@Nonnull MetaDocPart metaDocPart);
    @Nonnull Collection<InternalField<?>> getInternalFields(@Nonnull TableRef tableRef);
    @Nonnull Collection<InternalField<?>> getPrimaryKeyInternalFields(@Nonnull TableRef tableRef);
    @Nonnull Collection<InternalField<?>> getReferenceInternalFields(@Nonnull TableRef tableRef);
    @Nonnull Collection<InternalField<?>> getForeignInternalFields(@Nonnull TableRef tableRef);
    @Nonnull Collection<InternalField<?>> getReadInternalFields(@Nonnull MetaDocPart metaDocPart);
    @Nonnull Collection<InternalField<?>> getReadInternalFields(@Nonnull TableRef tableRef);
    
    long getDatabaseSize(@Nonnull DSLContext dsl, @Nonnull MetaDatabase database);
    long getCollectionSize(@Nonnull DSLContext dsl, @Nonnull MetaDatabase database, @Nonnull MetaCollection collection);
    long getDocumentsSize(@Nonnull DSLContext dsl, @Nonnull MetaDatabase database, @Nonnull MetaCollection collection);
    Long getIndexSize(@Nonnull DSLContext dsl, @Nonnull MetaDatabase database, @Nonnull MetaCollection collection, @Nonnull String index);

    Optional<String> readKV(@Nonnull DSLContext dsl, @Nonnull MetaInfoKey key);
    Stream<MetaDatabaseRecord> readMetaDatabaseTable(DSLContext dsl);

    /**
     *
     * @return
     */
    default List<SemanticTable<?>> getMetaTables() {
        return Lists.newArrayList(
                getKvTable(),
                getMetaDocPartIndexColumnTable(),
                getMetaDocPartIndexTable(),
                getMetaIndexFieldTable(),
                getMetaScalarTable(),
                getMetaFieldTable(),
                getMetaDocPartTable(),
                getMetaIndexTable(),
                getMetaCollectionTable(),
                getMetaDatabaseTable()
        );
    }
}
