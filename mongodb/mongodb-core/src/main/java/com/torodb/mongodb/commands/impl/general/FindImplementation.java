/*
 * ToroDB - ToroDB-poc: MongoDB Core
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
package com.torodb.mongodb.commands.impl.general;

import java.util.List;
import java.util.OptionalLong;

import javax.inject.Singleton;

import com.eightkdata.mongowp.ErrorCode;
import com.eightkdata.mongowp.Status;
import com.eightkdata.mongowp.bson.BsonDocument;
import com.eightkdata.mongowp.exceptions.CommandFailed;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.general.FindCommand;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.general.FindCommand.FindArgument;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.general.FindCommand.FindResult;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.pojos.CursorResult;
import com.eightkdata.mongowp.server.api.Command;
import com.eightkdata.mongowp.server.api.Request;
import com.torodb.core.cursors.Cursor;
import com.torodb.core.language.AttributeReference;
import com.torodb.core.language.AttributeReference.Builder;
import com.torodb.kvdocument.conversion.mongowp.ToBsonDocumentTranslator;
import com.torodb.kvdocument.values.KVDocument;
import com.torodb.kvdocument.values.KVValue;
import com.torodb.mongodb.commands.impl.ReadTorodbCommandImpl;
import com.torodb.mongodb.core.MongodTransaction;
import com.torodb.torod.TorodTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 */
@Singleton
public class FindImplementation implements ReadTorodbCommandImpl<FindArgument, FindResult> {

    private static final Logger LOGGER = LogManager.getLogger(FindImplementation.class);

    @Override
    public Status<FindResult> apply(Request req, Command<? super FindArgument, ? super FindResult> command, FindArgument arg, MongodTransaction context) {
        logFindCommand(arg);

        BsonDocument filter = arg.getFilter();

        Cursor<BsonDocument> cursor;

        switch (filter.size()) {
            case 0: {
                cursor = context.getTorodTransaction().findAll(req.getDatabase(), arg.getCollection())
                        .asDocCursor()
                        .transform(t -> t.getRoot())
                        .transform(ToBsonDocumentTranslator.getInstance());
                break;
            }
            case 1: {
                try {
                    cursor = getByAttributeCursor(context.getTorodTransaction(), req.getDatabase(), arg.getCollection(), filter)
                            .transform(ToBsonDocumentTranslator.getInstance());
                } catch (CommandFailed ex) {
                    return Status.from(ex);
                }
                break;
            }
            default: {
                return Status.from(ErrorCode.COMMAND_FAILED, "The given query is not supported right now");
            }
        }

        if (Long.valueOf(arg.getBatchSize()) > (long) Integer.MAX_VALUE) {
            return Status.from(ErrorCode.COMMAND_FAILED, "Only batchSize equals or lower than " + Integer.MAX_VALUE + " is supported");
        }
        
        OptionalLong batchSize = arg.getEffectiveBatchSize();
        List<BsonDocument> batch = cursor.getNextBatch(batchSize.isPresent() ? (int) batchSize.getAsLong() : 101);
        cursor.close();

        return Status.ok(new FindResult(CursorResult.createSingleBatchCursor(req.getDatabase(), arg.getCollection(), batch.iterator())));

    }

    private Cursor<KVDocument> getByAttributeCursor(TorodTransaction transaction, String db, String col, BsonDocument filter) throws CommandFailed {

        Builder refBuilder = new AttributeReference.Builder();
        KVValue<?> kvValue = AttrRefHelper.calculateValueAndAttRef(filter, refBuilder);

        return transaction.findByAttRef(db, col, refBuilder.build(), kvValue)
                .asDocCursor()
                .transform(t -> t.getRoot());
    }

    private void logFindCommand(FindArgument arg) {
        if (LOGGER.isTraceEnabled()) {
            String collection = arg.getCollection();
            String filter = arg.getFilter().toString();

            LOGGER.trace("Find into {} filter {}", collection, filter);
        }
    }

}
