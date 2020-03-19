
package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.values.KV;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;

class LeftJoin extends DoFn<KV<String, CoGbkResult>, String>
{
	
	@ProcessElement
	public void processElement(ProcessContext c) throws Exception {
  {	
	   KV<String, CoGbkResult> e = c.element();
	    
	    String key=e.getKey();
	    List<List<TableRow>> result = (List<List<TableRow>>) e.getValue();

	    List<TableRow> pt1Val = result.get(0);
	    List<TableRow> pt2Val = result.get(1);

	    if(pt1Val != null & pt2Val != null) 
         {
	    	
	    	List<TableRow> list = new ArrayList<>();

	    	list.addAll(pt1Val);
	    	list.addAll(pt2Val);
	    	//a. unique_key,a. complaint_type,a. complaint_description,b. status,b. status_change_date
	    	TableRow list1 = list.get(0);
	    	TableRow list2 = list.get(1);
	    	
	    	TableRow row = new TableRow();
	    	
	    	
          	//for (int i = 0; i < Table_Schema.class.getFields().length; i++) 
          	//{
          		
          	//	TableFieldSchema col = Table_Schema.getTableSchema().getFields().get(i);
            //    row.set(col.getName(),list1.get(col.getName()));
         	//}
       
	    	
	    	
	    	c.output(list1.toString());
	    	
    	
         }
  		}
    }
  }


