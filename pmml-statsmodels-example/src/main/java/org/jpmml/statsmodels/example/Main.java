/*
 * Copyright (c) 2019 Villu Ruusmann
 *
 * This file is part of JPMML-StatsModels
 *
 * JPMML-StatsModels is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-StatsModels is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-StatsModels.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.statsmodels.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.dmg.pmml.PMML;
import org.jpmml.model.metro.MetroJAXBUtil;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.python.PickleUtil;
import org.jpmml.python.Storage;
import org.jpmml.python.StorageUtil;
import org.jpmml.statsmodels.StatsModelsEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statsmodels.ResultsWrapper;

public class Main {

	@Parameter (
		names = "--help",
		description = "Show the list of configuration options and exit",
		help = true
	)
	private boolean help = false;

	@Parameter (
		names = {"--pkl-input"},
		description = "Pickle input file",
		required = true
	)
	private File input = null;

	@Parameter (
		names = "--pmml-output",
		description = "PMML output file",
		required = true
	)
	private File output = null;


	static
	public void main(String... args) throws Exception {
		Main main = new Main();

		JCommander commander = new JCommander(main);
		commander.setProgramName(Main.class.getName());

		try {
			commander.parse(args);
		} catch(ParameterException pe){
			StringBuilder sb = new StringBuilder();

			sb.append(pe.toString());
			sb.append("\n");

			commander.usage(sb);

			System.err.println(sb.toString());

			System.exit(-1);
		}

		if(main.help){
			StringBuilder sb = new StringBuilder();

			commander.usage(sb);

			System.out.println(sb.toString());

			System.exit(0);
		}

		main.run();
	}

	public void run() throws Exception {
		StatsModelsEncoder encoder = new StatsModelsEncoder();

		Object object;

		try(Storage storage = StorageUtil.createStorage(this.input)){
			logger.info("Parsing PKL..");

			long begin = System.currentTimeMillis();
			object = PickleUtil.unpickle(storage);
			long end = System.currentTimeMillis();

			logger.info("Parsed PKL in {} ms.", (end - begin));
		} catch(Exception e){
			logger.error("Failed to parse PKL", e);

			throw e;
		}

		if(!(object instanceof ResultsWrapper)){
			throw new IllegalArgumentException("The object (" + ClassDictUtil.formatClass(object) + ") is not a ResultsWrapper");
		}

		ResultsWrapper resultsWrapper = (ResultsWrapper)object;

		PMML pmml;

		try {
			logger.info("Converting..");

			long begin = System.currentTimeMillis();
			pmml = resultsWrapper.encodePMML(encoder);
			long end = System.currentTimeMillis();

			logger.info("Converted in {} ms.", (end - begin));
		} catch(Exception e){
			logger.error("Failed to convert", e);

			throw e;
		}

		try(OutputStream os = new FileOutputStream(this.output)){
			logger.info("Marshalling PMML..");

			long begin = System.currentTimeMillis();
			MetroJAXBUtil.marshalPMML(pmml, os);
			long end = System.currentTimeMillis();

			logger.info("Marshalled PMML in {} ms.", (end - begin));
		} catch(Exception e){
			logger.error("Failed to marshal PMML", e);

			throw e;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
}