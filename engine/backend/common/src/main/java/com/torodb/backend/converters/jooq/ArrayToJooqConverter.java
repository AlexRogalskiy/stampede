/*
 * ToroDB - ToroDB: Backend common
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
package com.torodb.backend.converters.jooq;

import javax.json.JsonValue;

import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.impl.DefaultDataType;

import com.torodb.backend.converters.array.ArrayConverter;
import com.torodb.kvdocument.values.KVValue;

public class ArrayToJooqConverter<UT extends KVValue<?>> implements Converter<String, UT> {
    
    private static final long serialVersionUID = 1L;

    public static <UT extends KVValue<?>, V extends JsonValue> DataType<UT> fromScalarValue(final Class<UT> type, final ArrayConverter<V, UT> arrayConverter, String typeName) {
        Converter<String, UT> converter = new ArrayToJooqConverter<>(type, arrayConverter);
        return new DefaultDataType<String>(null, String.class, typeName).asConvertedDataType(converter);
    }
    
    private final Class<UT> type;
    private final ArrayConverter<?, UT> arrayConverter;
    
    public ArrayToJooqConverter(Class<UT> type, ArrayConverter<?, UT> arrayConverter) {
        super();
        this.type = type;
        this.arrayConverter = arrayConverter;
    }
    public UT from(String databaseObject) {
        throw new RuntimeException("This conversor should not be used to convert from a database object");
    }
    public String to(UT userObject) {
        return arrayConverter.toJsonLiteral(userObject);
    }
    public Class<String> fromType() {
        return String.class;
    }
    public Class<UT> toType() {
        return type;
    }
}
