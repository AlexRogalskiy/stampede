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
import com.torodb.backend.postgresql.converters.jooq.binding.JsonbBinding;
import com.torodb.backend.postgresql.converters.sql.JsonbSqlBinding;
import com.torodb.backend.postgresql.converters.sql.StringSqlBinding;
import com.torodb.kvdocument.types.JavascriptWithScopeType;
import com.torodb.kvdocument.types.KvType;
import com.torodb.kvdocument.types.StringType;
import com.torodb.kvdocument.values.*;
import com.torodb.kvdocument.values.heap.MapKvDocument;
import org.jooq.impl.SQLDataType;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;

/**
 *
 */
public class JavascriptWithScopeValueConverter implements KvValueConverter<String, String, KvJavascriptWithScope> {

  private static final long serialVersionUID = 1L;

  public static final DataTypeForKv<KvJavascriptWithScope> TYPE = JsonbBinding.fromKvValue(KvJavascriptWithScope.class,
          new JavascriptWithScopeValueConverter());

  @Override
  public KvType getErasuredType() {
    return JavascriptWithScopeType.INSTANCE;
  }

  @Override
  public KvJavascriptWithScope from(String databaseObject) {

    final JsonReader reader = Json.createReader(new ByteArrayInputStream(databaseObject.getBytes()));
    JsonObject object = reader.readObject();

    //need to discuss implementation of scope
    return KvJavascriptWithScope.of(object.getString("js"), object.getString("scope"));
  }

  @Override
  public String to(KvJavascriptWithScope userObject) {
    return Json.createObjectBuilder()
            .add("js", userObject.getJs())
            .add("scope", userObject.getScope())
            .build()
            .toString();

  }

  @Override
  public Class<String> fromType() {
    return String.class;
  }

  @Override
  public Class<KvJavascriptWithScope> toType() {
    return KvJavascriptWithScope.class;
  }

  @Override
  public SqlBinding<String> getSqlBinding() {
    return JsonbSqlBinding.INSTANCE;
  }

}
