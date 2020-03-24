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
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
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
		//FileSystems.setDefaultPipelineOptions(options);

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
	  	//final TupleTag<TableRow> table3Tag = new TupleTag<>();

	  	 	
	  //Merge collection values into a CoGbkResult collection
	  	PCollection<KV<String, CoGbkResult>> coGbkResult = KeyedPCollectionTuple
	  	    .of(table1Tag, table1Rows)
	  	    .and(table2Tag, table2Rows)
	  	    //.and(table3Tag, table3Rows)
	  	    .apply("joinkey", CoGroupByKey.create());
	  	    
	  	
	  //Final coGroup Result
	  	coGbkResult.apply("ProcessResults", 
	  		    ParDo.of(new DoFn<KV<String, CoGbkResult>, TableRow>()
	  	{

			private static final long serialVersionUID = 1L;
			@ProcessElement
	  		public void processElement(ProcessContext c) throws Exception {
	  	    {	
	  		    KV<String, CoGbkResult> e = c.element();
	  		    String key=c.element().getKey();
	  		    CoGbkResult result =  e.getValue();
	  		    
	  		    List<TableRow> pt1Val = (List<TableRow>) result.getAll(table1Tag);
	  		    List<TableRow> pt2Val = (List<TableRow>) result.getAll(table2Tag);

	  		    TableRow row = new TableRow();
	  		    TableRow row1 = new TableRow();
	  		  
	  		    TableFieldSchema col = new TableFieldSchema();
	  		    TableFieldSchema col1 = new TableFieldSchema();
	  		  
	  			  		  
	  		  if(pt1Val != null & pt2Val != null) 
	  		    {
	  		    	
	  		    	for(TableRow a : pt1Val) 
	  		    	{
	  		    		if(a.values()!=null)
	  		    		{    		
	  		    		for (int i = 0; i < 5 ; i++) 
	  		    			{
	  		    			 col = Table_Schema.getTableSchema().getFields().get(i);
	  		    			 row.set(col.getName(), a.get(col.getName()));
	  		    			}
	  		    		
	  		    		}
	  		    	}
	  		    	
	  		  	for(TableRow b : pt2Val) 
  		    	{
  		    		if(b.values()!=null)
  		    		{    		
  		    		for (int i = 0; i < 5 ; i++) 
  		    			{
  		    			 col1 = Table_Schema.getTableSchema().getFields().get(i);
  		    			 row1.set(col1.getName(), b.get(col1.getName()));
  		    			}
  		    		
  		    		}
  		    	}

	  		  	//Right Outer Join
	  		    	//if(row.isEmpty())	
	  		    		//{c.output(row);}
	  		    	
	  		    //Left Outer Join
	  		    	//if(row1.isEmpty())	
	  		    		//{c.output(row1);}
	  		  	
	  		    //Inner Join
  		        	if(!row.isEmpty() && !row1.isEmpty())	
  		    	    	{c.output(row);}
	  
	  	
	  		      } 	
	  	         }
	  	  		}
	  	    
	  	  }))
	  		//.apply(TextIO.write().to(PropertyUtil.getProperty("dataflow.job.gcswritefile")));
	  	
	  	  .apply("WriteToBq", BigQueryIO.writeTableRows()
	             .to(PropertyUtil.getProperty("dataflow.job.tablename"))
	            .withWriteDisposition(WriteDisposition.WRITE_TRUNCATE)
	              .withCreateDisposition(CreateDisposition.CREATE_NEVER));
		  	


	  	pipeline.run().waitUntilFinish();
	}

}

