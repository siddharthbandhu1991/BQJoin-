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
	        
	        fields.add(new TableFieldSchema().setName("trip_id").setType("STRING"));
	        fields.add(new TableFieldSchema().setName("shipper_id").setType("STRING"));
	        fields.add(new TableFieldSchema().setName("start_station_id").setType("STRING"));
	        fields.add(new TableFieldSchema().setName("start_station_name").setType("STRING"));
	        
	        return new TableSchema().setFields(fields); 
	    }


}
