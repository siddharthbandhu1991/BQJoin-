package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.CreateDisposition;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TypeDescriptors;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;

public class BQJoin {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
		DefineProperties.configurePipeLineOptions(options);
		FileSystems.setDefaultPipelineOptions(options);

	  	final Pipeline pipeline = Pipeline.create(options);
	  	
	    	  		
	  	//PCollection<TableRow> tableRow1 = pipeline.apply(BigQueryIO.read().fromQuery(PropertyUtil.getProperty("dataflow.job.query1")));
	  	//PCollection<TableRow> tableRow2 = pipeline.apply(BigQueryIO.read().fromQuery(PropertyUtil.getProperty("dataflow.job.query2")));
  		//tableRow1.apply("ConverToString",ParDo.of(new TableRowtoString()))
		//.apply(TextIO.write().to(PropertyUtil.getProperty("dataflow.job.gcswritefile")));		
	  	
	  	String key = PropertyUtil.getProperty("dataflow.job.joinkey");
	  	
	  	WithKeys<String, TableRow> joinkey = WithKeys.of(
	  		    (TableRow row) ->
	  		        String.format("%s",
	  		            row.get(key)))
	  		    .withKeyType(TypeDescriptors.strings());
	  	
	  	
	  	PCollection<KV<String, TableRow>> table1Rows = pipeline
	  		    .apply("ReadTable1",BigQueryIO.read().fromQuery(PropertyUtil.getProperty("dataflow.job.query1")))
	  		    .apply("WithKeys", joinkey);

	  	PCollection<KV<String, TableRow>> table2Rows = pipeline
		  		.apply("ReadTable2",BigQueryIO.read().fromQuery(PropertyUtil.getProperty("dataflow.job.query2")))
		  	    .apply("WithKeys", joinkey);

		final TupleTag<TableRow> table1Tag = new TupleTag<>();
	  	final TupleTag<TableRow> table2Tag = new TupleTag<>();
	  	 	
	  //Merge collection values into a CoGbkResult collection
	  	PCollection<KV<String, CoGbkResult>> coGbkResult = KeyedPCollectionTuple
	  	    .of(table1Tag, table1Rows)
	  	    .and(table2Tag, table2Rows)
	  	    .apply("joinkey", CoGroupByKey.create());
	  	
	  	

	  	
	  //Final cogroup Result
	  	coGbkResult.apply("ConverToBqRow",ParDo.of(new LeftJoin()))
	  		.apply(TextIO.write().to(PropertyUtil.getProperty("dataflow.job.gcswritefile")));
	  	
	  //	.apply("WriteToBq", BigQueryIO.writeTableRows()
	        //      .to(PropertyUtil.getProperty("dataflow.job.tablename"))
	       //         .withWriteDisposition(WriteDisposition.WRITE_APPEND)
	        //       .withCreateDisposition(CreateDisposition.CREATE_NEVER));
		  	



	  	pipeline.run().waitUntilFinish();
	}

}
