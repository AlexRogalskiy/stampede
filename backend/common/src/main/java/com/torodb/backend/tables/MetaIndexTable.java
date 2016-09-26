package com.torodb.backend.tables;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import com.torodb.backend.meta.TorodbSchema;
import com.torodb.backend.tables.records.MetaIndexRecord;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(
        value = "HE_HASHCODE_NO_EQUALS",
        justification
        = "Equals comparation is done in TableImpl class, which compares schema, name and fields"
)
public abstract class MetaIndexTable<R extends MetaIndexRecord> extends SemanticTable<R> {

    private static final long serialVersionUID = 230691041;

    public static final String TABLE_NAME = "index";

    public enum TableFields {
        DATABASE        (   "database"          ),
        COLLECTION      (   "collection"        ),
        NAME            (   "name"              ),
        UNIQUE          (   "unique"            )
        ;

        public final String fieldName;

        TableFields(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String toString() {
            return fieldName;
        }
    }

    /**
     * The class holding records for this type
     * @return 
     */
    @Override
    public abstract Class<R> getRecordType();

    /**
     * The column <code>torodb.index.database</code>.
     */
    public final TableField<R, String> DATABASE 
            = createDatabaseField();

    /**
     * The column <code>torodb.index.collection</code>.
     */
    public final TableField<R, String> COLLECTION 
            = createCollectionField();

    /**
     * The column <code>torodb.index.name</code>.
     */
    public final TableField<R, String> NAME 
            = createNameField();

    /**
     * The column <code>torodb.index.unique</code>.
     */
    public final TableField<R, Boolean> UNIQUE 
            = createUniqueField();

    protected abstract TableField<R, String> createDatabaseField();
    protected abstract TableField<R, String> createCollectionField();
    protected abstract TableField<R, String> createNameField();
    protected abstract TableField<R, Boolean> createUniqueField();

    private final UniqueKeys<R> uniqueKeys;
    
    /**
     * Create a <code>torodb.index</code> table reference
     */
    public MetaIndexTable() {
        this(TABLE_NAME, null);
    }

    protected MetaIndexTable(String alias, Table<R> aliased) {
        this(alias, aliased, null);
    }

    protected MetaIndexTable(String alias, Table<R> aliased, Field<?>[] parameters) {
        super(alias, TorodbSchema.TORODB, aliased, parameters, "");
        
        this.uniqueKeys = new UniqueKeys<R>(this);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<R> getPrimaryKey() {
        return uniqueKeys.INDEX_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<R>> getKeys() {
        return Arrays.<UniqueKey<R>>asList(uniqueKeys.INDEX_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract MetaIndexTable<R> as(String alias);

    /**
     * Rename this table
     */
    public abstract MetaIndexTable<R> rename(String name);
    
    public UniqueKeys<R> getUniqueKeys() {
        return uniqueKeys;
    }
    
    public static class UniqueKeys<KeyRecord extends MetaIndexRecord> extends AbstractKeys {
        private final UniqueKey<KeyRecord> INDEX_PKEY;
        
        private UniqueKeys(MetaIndexTable<KeyRecord> indexTable) {
            INDEX_PKEY = createUniqueKey(indexTable, indexTable.DATABASE, indexTable.COLLECTION, indexTable.NAME);
        }
    }
}
