# dng-type-system-management

Commandline tool to support managing Doors Next Type Systems.

Provides a conmmand **exportConfigurations** to export the streams/configurations for components of a project area as target into a source to target mapping CSV file.

Provides a conmmand **exportConfigurationsByDescription** to export a source to target mapping for the configurations of the components of a project area to a CSV file, where the configurations are streams and marked with a source or target tag as substring in the description.

Provides a conmmand **exportAllConfigurationsByDescription** to export a source to target mapping for the configurations of the components of all project areas to a CSV file, where the configurations are streams and marked with a source or target tag as substring in the description.

Provides a command **importTypeSystem** to import a RM Type System from streams/configurations of a component into streams/configurations of other components using a CSV/Excel file describing the source and target configurations.

Provides a command **deliverTypeSystem** to deliver the changes to a type system in source streams/configurations of components to other streams/configurations for this component using a CSV/Excel file describing the source and target configurations.

## Manual Workflow

1. Export the streams/configurations to a CSV file using exportConfigurations.
2. Open the CSV file with an editor e.g. Notepad++ or Excel.
3. Find the configuration that is the source for the operation and copy the URL from the "target" column to use it as source configuration.
4. Paste this configuration URL into the "source" column's cells in the desired rows for the target streams/configurations. 
5. Delete all rows with no source configuration URL.
6. Save the CSV/Excel file.                                                                              
7. Run the desired command importTypeSystem to import the type system or deliverTypeSystem to deliver the type system changes providing the CSV file as input.

## Automated Workflow

1. Use and maintain pairs of "tag" strings such as -sourceTag TSDSource_TS1 -targetTag TSDTarget_TS1 in the descriptions of streams
2. Export the streams/configurations to a CSV file using exportConfigurationsByDescription or exportAllConfigurationsByDescription.
3. Run the desired command importTypeSystem to import the type system or deliverTypeSystem to deliver the type system changes providing the CSV file as input.

## Example parameters:

-command exportConfigurations -url https://clm.example.com:9443/rm -user jim -password ********** -project "GC JKE Banking (Requirements Management)" -csvfile export.csv -csvDelimiter ";"

-command exportConfigurationsByDescription -url https://clm.example.com:9443/rm -user ralph -password ********** -project "GC JKE Banking (Requirements Management)" -sourceTag TSSource_TS1 -targetTag TSTarget_TS1 -csvfile export_description.csv -csvDelimiter ";"

-command exportAllConfigurationsByDescription -url https://clm.example.com:9443/rm -user ralph -password ********** -sourceTag TSSource_TS1 -targetTag TSTarget_TS1 -csvfile export_description.csv -csvDelimiter ";"

-command importTypeSystem -url https://clm.example.com:9443/rm -user jim -password ********** -csvfile TypeSystemImport.csv -csvDelimiter ";"

-command deliverTypeSystem -url https://clm.example.com:9443/rm -user jim -password ********** -csvfile TypeSystemDelivery.csv -csvDelimiter ";"

## License
This software is licensed under the Eclipse Public License: [Eclipse Public License - v 1.0](com.ibm.rm.typemanagement/LICENSE.html)

The code is dependent on and uses the following packages

## Dependent on 
The code usses the following packages published under the following licenses

| Package          | Version      | License                                                         |
|------------------|--------------|-----------------------------------------------------------------|
| slf4j-api        | 1.7.25       | License MIT https://www.slf4j.org/license.html |
| slf4j-log4j12    | 1.7.25	      | License MIT https://www.slf4j.org/license.html |
| wink-client      | 1.4	        | Apache License http://www.apache.org/licenses/ |
| commons-cli      | 1.2	        | Apache License 2.0 https://www.apache.org/licenses/LICENSE-2.0 |
| commons-io       | 1.3.2        | Apache License 2.0 https://www.apache.org/licenses/LICENSE-2.0 |
| oslc-java-client | 2.3.0        | Eclipse EPL, EDL https://wiki.eclipse.org/Lyo/Licenses |
| xml-apis         | 1.4.01	      | Apache License 2.0 http://xerces.apache.org/xml-commons/licenses.html |
| javax.json       | 1.1.2	      | CDDL 1.1 GPL 2.0 https://goo.gl/JhfxP5 |
| opencsv          | 4.2          | Apache License 2.0 http://opencsv.sourceforge.net/license.html |
