/*
 * ToroDB - ToroDB-poc: MongoDB Repl
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
package com.torodb.mongodb.repl;

import java.util.Locale;

import javax.inject.Inject;

import com.codahale.metrics.Counter;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.pojos.MemberState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.torodb.core.metrics.MetricNameFactory;
import com.torodb.core.metrics.SettableGauge;
import com.torodb.core.metrics.ToroMetricRegistry;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ReplMetrics {

	private static final MetricNameFactory factory = new MetricNameFactory("Repl");
	private final SettableGauge<String> memberState;
    private final ImmutableMap<MemberState, Counter> memberStateCounters;
    private final SettableGauge<String> lastOpTimeFetched;
    private final SettableGauge<String> lastOpTimeApplied;
	
	@Inject
	public ReplMetrics(ToroMetricRegistry registry){
        memberState = registry.gauge(factory.createMetricName("currentMemberState"));
        ImmutableMap.Builder<MemberState, Counter> memberStateCountersBuilder =
                ImmutableMap.builder();
	    for (MemberState memberState : MemberState.values()) {
	        memberStateCountersBuilder.put(memberState, 
	                registry.counter(factory.createMetricName(
	                        memberState.name().substring(3).toLowerCase(Locale.US) + "Count")));
	    }
        memberStateCounters = Maps.immutableEnumMap(memberStateCountersBuilder.build());
        lastOpTimeFetched = registry.gauge(factory.createMetricName("lastOpTimeFetched"));
        lastOpTimeApplied = registry.gauge(factory.createMetricName("lastOpTimeApplied"));
	}

    public SettableGauge<String> getMemberState() {
        return memberState;
    }

    public ImmutableMap<MemberState, Counter> getMemberStateCounters() {
        return memberStateCounters;
    }

    public SettableGauge<String> getLastOpTimeFetched() {
        return lastOpTimeFetched;
    }

    public SettableGauge<String> getLastOpTimeApplied() {
        return lastOpTimeApplied;
    }
}
