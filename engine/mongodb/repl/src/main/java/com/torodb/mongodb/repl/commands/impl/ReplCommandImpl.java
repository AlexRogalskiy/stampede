/*
 * ToroDB - ToroDB: MongoDB Repl
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
package com.torodb.mongodb.repl.commands.impl;

import org.apache.logging.log4j.Logger;

import com.eightkdata.mongowp.server.api.Command;
import com.eightkdata.mongowp.server.api.CommandImplementation;
import com.torodb.torod.SharedWriteTorodTransaction;

/**
 *
 */
public abstract  class ReplCommandImpl<Arg, Res> implements CommandImplementation<Arg, Res, SharedWriteTorodTransaction>{

    protected void reportErrorIgnored(Logger logger, Command<?, ?> cmd, Throwable t) {
        logger.warn(cmd.getCommandName() + " command execution failed. "
                    + "Ignoring it", t);
    }

}
