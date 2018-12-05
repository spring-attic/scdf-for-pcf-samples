# Sample Applications for SCDF for PCF

This repository contains samples for use with [SCDF for PCF](https://docs.pivotal.io/scdf).

## Creating a SCDF for PCF Service Instance

The sample app requires the `cf` CLI to be a targeted and authenticated to a PCF foundry with the SCDF for PCF tile
installed. To create a SCDF for PCF service instance run:

```
$ cf create-service p-dataflow standard dataflow
``` 

## Building

To build the sample app, simply run:

```
$ ./mvnw package
```

## Deploying API Sample

1. Deploy the API Sample app by running:
	
	```
	$ cf push api-sample -p api-sample/target/api-sample.jar --no-start
	```
	
	This will create an application named `api-sample` and upload the API Sample Spring Boot application.

1. Bind to the Spring Cloud Data Flow Service Instance running:

	```
	$ cf bind-service api-sample dataflow
	```
	
	If your service instance has a name other than `dataflow`, you will need to run something like:
	
	```
	$ cf bind-service api-sample YOUR_DATAFLOW_SI --binding-name dataflow
	```
	
	**Note:** The `binding-name` option is only available on PCF 2.1 and higher.

1. Start the API Sample app by running:
	```
	$ cf start api-sample
	```

The API Sample application has two REST endpoints: `/version` and `/apps`. Each of these endpoints invoke the Data
Flow server's API. See the `DemoApplication` class for more details.