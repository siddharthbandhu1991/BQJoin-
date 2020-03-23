package com;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
//a. unique_key,a. complaint_type,a. complaint_description,b. status,b. status_change_date

public class Table_Schema 
{

	  static TableSchema getTableSchema() {
		   List<TableFieldSchema> fields = new ArrayList<TableFieldSchema>();
	        
	        fields.add(new TableFieldSchema().setName("unique_key").setType("STRING"));
	       // fields.add(new TableFieldSchema().setName("complaint_type").setType("STRING"));
	       // fields.add(new TableFieldSchema().setName("complaint_description").setType("STRING"));
	        //fields.add(new TableFieldSchema().setName("status").setType("STRING"));
	       // fields.add(new TableFieldSchema().setName("status_change_date").setType("STRING"));

	        return new TableSchema().setFields(fields);
	    }


}
