package com;


import java.io.IOException;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;



public final class DefineProperties 
{
	private DefineProperties() 
	{
		
	}

	
	public static void configurePipeLineOptions(final DataflowPipelineOptions options) throws IOException
	{
		options.setProject(PropertyUtil.getProperty("dataflow.job.project.id"));
		options.setJobName(PropertyUtil.getProperty("dataflow.job.name"));
		
		options.setTempLocation(PropertyUtil.getProperty("dataflow.job.temp.location"));
		options.setStagingLocation(PropertyUtil.getProperty("dataflow.job.stg.location"));
		
		options.setMaxNumWorkers(Integer.parseInt(PropertyUtil.getProperty("dataflow.job.worker")));
		options.setServiceAccount("owner-677@causal-root-268810.iam.gserviceaccount.com");
		options.setRegion("us-east1");
		options.setRunner(DataflowRunner.class);


	}
	
	
}
