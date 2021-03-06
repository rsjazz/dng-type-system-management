/*******************************************************************************
 * Copyright (c) 2012 - 2013, 2018 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *    Ralph Schoon - Initial implementation
 *******************************************************************************/
package com.ibm.requirement.typemanagement.oslc.client.automation.commands;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.http.HttpStatus;
import org.eclipse.lyo.client.oslc.OSLCConstants;
import org.eclipse.lyo.client.oslc.jazz.JazzFormAuthClient;
import org.eclipse.lyo.client.oslc.jazz.JazzRootServicesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.requirement.typemanagement.oslc.client.automation.DngTypeSystemManagementConstants;
import com.ibm.requirement.typemanagement.oslc.client.automation.framework.AbstractCommand;
import com.ibm.requirement.typemanagement.oslc.client.automation.framework.ContainsStringRule;
import com.ibm.requirement.typemanagement.oslc.client.automation.framework.IRule;
import com.ibm.requirement.typemanagement.oslc.client.automation.util.CsvExportImportInformation;
import com.ibm.requirement.typemanagement.oslc.client.automation.util.CsvUtil;
import com.ibm.requirement.typemanagement.oslc.client.dngcm.DngCmUtil;
import com.ibm.requirement.typemanagement.oslc.client.dngcm.ProjectAreaOslcServiceProvider;
import com.ibm.requirement.typemanagement.oslc.client.resources.Component;
import com.ibm.requirement.typemanagement.oslc.client.resources.Configuration;

/**
 * Exports the streams/configurations of a project area to CSV/Excel.
 *
 */
public class ExportConfigurationsByDescription extends AbstractCommand {

	public static final Logger logger = LoggerFactory.getLogger(ExportConfigurationsByDescription.class);

	/**
	 * Create new command and give it the name
	 */
	public ExportConfigurationsByDescription() {
		super(DngTypeSystemManagementConstants.CMD_EXPORT_CONFIGURATIONS_BY_DESCRIPTION);
	}

	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_URL, true,
				DngTypeSystemManagementConstants.PARAMETER_URL_DESCRIPTION);
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_USER, true,
				DngTypeSystemManagementConstants.PARAMETER_USER_ID_DESCRIPTION);
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_PASSWORD, true,
				DngTypeSystemManagementConstants.PARAMETER_PASSWORD_DESCRIPTION);
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA, true,
				DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA_DESCRIPTION);
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_SOURCE_TAG, true,
				DngTypeSystemManagementConstants.PARAMETER_SOURCE_TAG_DESCRIPTION);
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_TARGET_TAG, true,
				DngTypeSystemManagementConstants.PARAMETER_TARGET_TAG_DESCRIPTION);
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH, true,
				DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH_DESCRIPTION);
		options.addOption(DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER, true,
				DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER_DESCRIPTION);
		return options;
	}

	@Override
	public boolean checkParameters(final CommandLine cmd) {
		boolean isValid = true;

		if (!(cmd.hasOption(DngTypeSystemManagementConstants.PARAMETER_URL)
				&& cmd.hasOption(DngTypeSystemManagementConstants.PARAMETER_USER)
				&& cmd.hasOption(DngTypeSystemManagementConstants.PARAMETER_PASSWORD)
				&& cmd.hasOption(DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA)
				&& cmd.hasOption(DngTypeSystemManagementConstants.PARAMETER_SOURCE_TAG)
				&& cmd.hasOption(DngTypeSystemManagementConstants.PARAMETER_TARGET_TAG)
				&& cmd.hasOption(DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH))) {
			isValid = false;
		}
		return isValid;
	}

	@Override
	public void printSyntax() {
		logger.info("{}", getCommandName());
		logger.info("\tSyntax : -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} [ -{} {} ]",
				DngTypeSystemManagementConstants.PARAMETER_COMMAND, getCommandName(),
				DngTypeSystemManagementConstants.PARAMETER_URL,
				DngTypeSystemManagementConstants.PARAMETER_URL_PROTOTYPE,
				DngTypeSystemManagementConstants.PARAMETER_USER,
				DngTypeSystemManagementConstants.PARAMETER_USER_PROTOTYPE,
				DngTypeSystemManagementConstants.PARAMETER_PASSWORD,
				DngTypeSystemManagementConstants.PARAMETER_PASSWORD_PROTOTYPE,
				DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA,
				DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA_PROTOTYPE,
				DngTypeSystemManagementConstants.PARAMETER_SOURCE_TAG,
				DngTypeSystemManagementConstants.PARAMETER_TAG_PROTOTYPE,
				DngTypeSystemManagementConstants.PARAMETER_TARGET_TAG,
				DngTypeSystemManagementConstants.PARAMETER_TAG_PROTOTYPE,
				DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH,
				DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH_PROTOTYPE,
				DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER,
				DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER_PROTOTYPE);
		logger.info("\tExample: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				DngTypeSystemManagementConstants.PARAMETER_COMMAND, getCommandName(),
				DngTypeSystemManagementConstants.PARAMETER_URL, DngTypeSystemManagementConstants.PARAMETER_URL_EXAMPLE,
				DngTypeSystemManagementConstants.PARAMETER_USER,
				DngTypeSystemManagementConstants.PARAMETER_USER_ID_EXAMPLE,
				DngTypeSystemManagementConstants.PARAMETER_PASSWORD,
				DngTypeSystemManagementConstants.PARAMETER_PASSWORD_EXAMPLE,
				DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA,
				DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA_EXAMPLE,
				DngTypeSystemManagementConstants.PARAMETER_SOURCE_TAG,
				DngTypeSystemManagementConstants.PARAMETER_SOURCE_TAG_EXAMPLE,
				DngTypeSystemManagementConstants.PARAMETER_TARGET_TAG,
				DngTypeSystemManagementConstants.PARAMETER_TARGET_TAG_EXAMPLE,
				DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH,
				DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH_EXAMPLE);

		logger.info("\tOptional parameter: -{} {}", DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER,
				DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER_PROTOTYPE);
		logger.info("\tExample optional parameter: -{} {}", DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER,
				DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER_EXAMPLE);
	}

	@Override
	public boolean execute() {

		boolean result = false;
		
		// Get all the option values
		String webContextUrl = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_URL);
		String user = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_USER);
		String passwd = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_PASSWORD);
		String projectAreaName = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_PROJECT_AREA);
		String sourceTag = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_SOURCE_TAG);
		String targetTag = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_TARGET_TAG);
		String csvFilePath = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_CSV_FILE_PATH);
		String csvDelimiter = getCmd().getOptionValue(DngTypeSystemManagementConstants.PARAMETER_CSV_DELIMITER);

		try {
			
			// Login
			JazzRootServicesHelper helper = new JazzRootServicesHelper(webContextUrl, OSLCConstants.OSLC_RM_V2);
			logger.trace("Login");
			String authUrl = webContextUrl.replaceFirst("/rm", "/jts");
			JazzFormAuthClient client = helper.initFormClient(user, passwd, authUrl);
			if (client.login() == HttpStatus.SC_OK) {

				// Get rootservices
				String catalogUrl = helper.getCatalogUrl();
				logger.info("Getting Configurations");
				
				// Get the OSLC CM Service Provider
				String cmCatalogUrl = DngCmUtil.getCmServiceProvider(helper);
				if (cmCatalogUrl == null) {
					logger.error("Unable to access the OSLC Configuration Management Provider URL for '{}'",
							webContextUrl);
					return result;
				}
				
				// Find the OSLC service provider for the project area - assuming the project area is CM enabled
				final ProjectAreaOslcServiceProvider rmProjectAreaOslcServiceProvider = ProjectAreaOslcServiceProvider
						.findProjectAreaOslcServiceProvider(client, catalogUrl, projectAreaName);
				if (rmProjectAreaOslcServiceProvider.getProjectAreaId() == null) {
					logger.error("Unable to find project area service provider for '{}'", projectAreaName);
					return result;
				}
				
				// Get the components and the configurations for the components
				Collection<Component> components = DngCmUtil.getComponentsForProjectArea(client, cmCatalogUrl,
						rmProjectAreaOslcServiceProvider.getProjectAreaId());
				Collection<Configuration> configurations = DngCmUtil.getConfigurationsForComponents(client, components);

				logger.info("Filtering for Configurations");
				IRule sourceRule = new ContainsStringRule(sourceTag);
				IRule targetRule = new ContainsStringRule(targetTag);
				List<CsvExportImportInformation> configurationList = getConfigurations(configurations, projectAreaName,
						sourceRule, targetRule);
				if (configurationList == null) {
					logger.info("No valid configuration data found.");
					return false;
				}

				// export the data
				CsvUtil csv = new CsvUtil();
				if (null != csvDelimiter && csvDelimiter != "") {
					csv.setSeperator(csvDelimiter.charAt(0));
				}

				logger.info("Exporting data to file '{}'.", csvFilePath);
				result = csv.exportConfigurationList(csvFilePath, configurationList);
				logger.trace("End");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Convert the configurations into the information needed for the export to
	 * prepare writing to CSV.
	 * 
	 * We are only interested in streams and the streams description must contain a
	 * tag specified b a rule.
	 * 
	 * @param configurations
	 * @param projectArea
	 * @param sourceRule
	 * @param targetRule
	 * @return
	 * @throws URISyntaxException
	 */
	private List<CsvExportImportInformation> getConfigurations(Collection<Configuration> configurations,
			String projectArea, IRule sourceRule, IRule targetRule) throws URISyntaxException {
		List<CsvExportImportInformation> configurationList = new ArrayList<CsvExportImportInformation>();
		Configuration source = null;
		for (Configuration config : configurations) {
			if (!config.isStream()) {
				continue;
			}
			if (targetRule.matches(config.getDescription())) {
				configurationList.add(new CsvExportImportInformation(null, config, projectArea));
			}
			if (sourceRule.matches(config.getDescription())) {
				if (source != null) {
					logger.info(
							"Ambiguous sources found source 1 URI '{}' title '{}' source 2 URI '{}' title '{}' exiting.",
							source.getAbout().toString(), source.getTitle(), config.getAbout().toString(),
							config.getTitle());
					return null;
				}
				source = config;
			}
		}
		if (source != null) {
			for (CsvExportImportInformation csvExportImportInformation : configurationList) {
				csvExportImportInformation.setSource(source.getAbout().toString());
			}
		} else {
			logger.info("No match for source.");
			return null;
		}
		return configurationList;
	}

}
