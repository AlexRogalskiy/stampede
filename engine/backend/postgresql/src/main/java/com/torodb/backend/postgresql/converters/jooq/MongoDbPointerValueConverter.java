/*
 * ToroDB
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.torodb.backend.postgresql.converters.jooq;

import com.torodb.backend.converters.jooq.DataTypeForKv;
import com.torodb.backend.converters.jooq.KvValueConverter;
import com.torodb.backend.converters.sql.SqlBinding;
import com.torodb.backend.postgresql.converters.PostgreSqlValueToCopyConverter;
import com.torodb.backend.postgresql.converters.sql.StringSqlBinding;
import com.torodb.kvdocument.types.KvType;
import com.torodb.kvdocument.types.MongoDbPointerType;
import com.torodb.kvdocument.types.StringType;
import com.torodb.kvdocument.values.KvMongoDbPointer;
import org.jooq.impl.SQLDataType;

/**
 *
 */
public class MongoDbPointerValueConverter implements KvValueConverter<String, String, KvMongoDbPointer> {

  private static final long serialVersionUID = 1L;

  public static final DataTypeForKv<KvMongoDbPointer> TYPE = DataTypeForKv.from(SQLDataType.VARCHAR,
      new MongoDbPointerValueConverter());

  @Override
  public KvType getErasuredType() {
    return MongoDbPointerType.INSTANCE;
  }

  @Override
  public KvMongoDbPointer from(String databaseObject) {
    //need to discuss implementation of options
    return KvMongoDbPointer.of(databaseObject, null);
  }

  @Override
  public String to(KvMongoDbPointer userObject) {
    StringBuilder sb = new StringBuilder();
    userObject.accept(PostgreSqlValueToCopyConverter.INSTANCE, sb);
    return sb.toString();
  }

  @Override
  public Class<String> fromType() {
    return String.class;
  }

  @Override
  public Class<KvMongoDbPointer> toType() {
    return KvMongoDbPointer.class;
  }

  @Override
  public SqlBinding<String> getSqlBinding() {
    return StringSqlBinding.INSTANCE;
  }

}
