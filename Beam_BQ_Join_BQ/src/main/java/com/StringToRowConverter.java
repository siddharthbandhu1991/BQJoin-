package com;

import org.apache.beam.sdk.transforms.DoFn;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;

class StringToRowConverter extends DoFn<TableRow, TableRow> 
{
		 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ProcessElement
     public void processElement(ProcessContext c) throws Exception {
         

          TableRow row = c.element();

            System.out.println(row);
             
         c.output(row);
      
     }
  }