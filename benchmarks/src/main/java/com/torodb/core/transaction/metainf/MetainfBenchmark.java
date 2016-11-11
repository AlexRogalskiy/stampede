/*
 * ToroDB - ToroDB-poc: Benchmarks
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
package com.torodb.core.transaction.metainf;

import com.torodb.core.TableRef;
import com.torodb.core.TableRefFactory;
import com.torodb.core.impl.TableRefFactoryImpl;
import com.torodb.core.transaction.metainf.MetainfoRepository.MergerStage;
import com.torodb.core.transaction.metainf.MetainfoRepository.SnapshotStage;
import com.torodb.metainfo.cache.mvcc.MvccMetainfoRepository;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@Fork(value = 1)
@Warmup(iterations = 4)
@Measurement(iterations = 10)
@BenchmarkMode(value = Mode.Throughput)
public class MetainfBenchmark {

    private static final TableRefFactory tableRefFactory = new TableRefFactoryImpl();
	private static final String DB = "test";
	private static final String COLL = "colltest";
	private static final String T1 = "t1";
	private static final String T2 = "t2";
	private static final String T3 = "t3";

	private MutableMetaSnapshot createMetaInfo(){
		MutableMetaSnapshot meta = new WrapperMutableMetaSnapshot(new ImmutableMetaSnapshot.Builder().build());
		MutableMetaDatabase database = meta.addMetaDatabase(DB, DB);
		MutableMetaCollection collection = database.addMetaCollection(COLL, COLL);

		TableRef t1=tableRefFactory.createRoot();
		MutableMetaDocPart docPartT1 = collection.addMetaDocPart(t1, T1);
			docPartT1.addMetaField("field1", "field1_s", FieldType.STRING);
			docPartT1.addMetaField("field2", "field2_i", FieldType.INTEGER);
			docPartT1.addMetaField("field3", "field3_d", FieldType.DOUBLE);
			docPartT1.addMetaField("field4", "field4_l", FieldType.LONG);
			docPartT1.addMetaField("field5", "field5_c", FieldType.CHILD);
			
		TableRef t2=tableRefFactory.createChild(t1, T2);
		MutableMetaDocPart docPartT2 = collection.addMetaDocPart(t2, T2);
			docPartT2.addMetaField("field1", "field1_s", FieldType.STRING);
			docPartT2.addMetaField("field2", "field2_i", FieldType.INTEGER);
			docPartT2.addMetaField("field3", "field3_d", FieldType.DOUBLE);
			docPartT2.addMetaField("field4", "field4_l", FieldType.LONG);
			docPartT2.addMetaField("field5", "field5_c", FieldType.CHILD);
		
		TableRef t3=tableRefFactory.createChild(t2, T3);
			MutableMetaDocPart docPartT3 = collection.addMetaDocPart(t3, T3);
			docPartT3.addMetaField("field1", "field1_s", FieldType.STRING);
			docPartT3.addMetaField("field2", "field2_i", FieldType.INTEGER);
			docPartT3.addMetaField("field3", "field3_d", FieldType.DOUBLE);
			docPartT3.addMetaField("field4", "field4_l", FieldType.LONG);
			
		return meta;
	}
	
	@Benchmark
	public void benchmarkImmutableCopyMetainfo(Blackhole blackhole) {
		MutableMetaSnapshot metaInfo = createMetaInfo();
		blackhole.consume(metaInfo.immutableCopy());
	}
	
	@Benchmark
	public void benchmarkSnapshotMetainfo(Blackhole blackhole) {
		MutableMetaSnapshot metaInfo = createMetaInfo();
		
		MvccMetainfoRepository mvccMetainfoRepository = new MvccMetainfoRepository(metaInfo.immutableCopy());
		MutableMetaSnapshot mutableSnapshot;
		try (SnapshotStage snapshot = mvccMetainfoRepository.startSnapshotStage()) {
			mutableSnapshot = snapshot.createMutableSnapshot();
		}
		blackhole.consume(mutableSnapshot);
	}
	

	@Benchmark
	public void benchmarkMergeSnapshotMetainfo(Blackhole blackhole) {
		MutableMetaSnapshot metaInfo = createMetaInfo();
		
		MvccMetainfoRepository metainfoRepository = new MvccMetainfoRepository(metaInfo.immutableCopy());
		MutableMetaSnapshot mutableSnapshot;
		try (SnapshotStage snapshot = metainfoRepository.startSnapshotStage()) {
			mutableSnapshot = snapshot.createMutableSnapshot();
		}

		MutableMetaDatabase dbMeta = mutableSnapshot.getMetaDatabaseByName(DB);
		MutableMetaCollection collection = dbMeta.getMetaCollectionByIdentifier(COLL);
		
		MutableMetaDocPart t1 = collection.getMetaDocPartByIdentifier(T1);
		t1.addMetaField("newone1", "newone1", FieldType.STRING);
		t1.addMetaField("newone2", "newone2", FieldType.STRING);
		t1.addMetaField("newone3", "newone3", FieldType.STRING);
		
		MutableMetaDocPart t2 = collection.getMetaDocPartByIdentifier(T2);
		t2.addMetaField("newone1", "newone1", FieldType.INTEGER);
		t2.addMetaField("newone2", "newone2", FieldType.INTEGER);
		t2.addMetaField("newone3", "newone3", FieldType.INTEGER);
		
		MutableMetaDocPart t3 = collection.getMetaDocPartByIdentifier(T3);
		t3.addMetaField("newone1", "newone1", FieldType.DOUBLE);
		t3.addMetaField("newone2", "newone2", FieldType.DOUBLE);
		t3.addMetaField("newone3", "newone3", FieldType.DOUBLE);
		
		try (MergerStage mergeStage = metainfoRepository.startMerge(mutableSnapshot)) {
            mergeStage.commit();
        } catch (UnmergeableException ex) {
            throw new AssertionError("Unmergeable changes", ex);
        }
	}
}
