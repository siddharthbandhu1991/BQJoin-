package com;

import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.beam.sdk.transforms.DoFn;


import com.google.api.services.bigquery.model.TableRow;

class TableRowtoString extends DoFn<TableRow, String>
{
        private static final long serialVersionUID = 1L;

			@ProcessElement
          public void processElement(ProcessContext c) throws Exception 
			{
              //String commaSep = c.element().values().stream().map(cell -> cell.toString().trim())
               //  .collect(Collectors.joining("\",\""));
              
				 String commaSep = c.element().values().toString();
              c.output(commaSep);
          
      }
}
