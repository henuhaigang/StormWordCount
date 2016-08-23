package com.gucl.storm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class LoadConfig {
	private static Logger logger = LoggerFactory.getLogger(LoadConfig.class);

	public static List<URL> findResources(String name) {
		try {
			Enumeration<URL> resources = Thread.currentThread()
					.getContextClassLoader().getResources(name);
			List<URL> ret = new ArrayList<URL>();
			while (resources.hasMoreElements()) {
				ret.add(resources.nextElement());
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param name
	 * @param mustExist
	 *            -- if this is true, the file must exist, otherwise throw
	 *            exception
	 * @param canMultiple
	 *            -- if this is false and there is multiple conf, it will throw
	 *            exception
	 * @return
	 */
	public static Map<String,Object> findAndReadYaml(String name, boolean mustExist,
			boolean canMultiple) {
		InputStream in = null;
		boolean confFileEmpty = false;
		try {
			in = getConfigFileInputStream(name, canMultiple);
			if (null != in) {
				Yaml yaml = new Yaml(new SafeConstructor());
				Map<String,Object> ret = (Map<String,Object>) yaml.load(new InputStreamReader(in));
				if (null != ret) {
					return new HashMap<String,Object>(ret);
				} else {
					confFileEmpty = true;
				}
			}

			if (mustExist) {
				if (confFileEmpty)
					throw new RuntimeException("Config file " + name
							+ " doesn't have any valid storm configs");
				else
					throw new RuntimeException(
							"Could not find config file on classpath " + name);
			} else {
				return new HashMap<String,Object>();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static InputStream getConfigFileInputStream(String configFilePath,
			boolean canMultiple) throws IOException {
		if (null == configFilePath) {
			throw new IOException(
					"Could not find config file, name not specified");
		}

		HashSet<URL> resources = new HashSet<URL>(findResources(configFilePath));
		if (resources.isEmpty()) {
			File configFile = new File(configFilePath);
			if (configFile.exists()) {
				return new FileInputStream(configFile);
			}
		} else if (resources.size() > 1 && canMultiple == false) {
			throw new IOException(
					"Found multiple "
							+ configFilePath
							+ " resources. You're probably bundling the Storm jars with your topology jar. "
							+ resources);
		} else {
			logger.info("Using " + configFilePath + " from resources");
			URL resource = resources.iterator().next();
			return resource.openStream();
		}
		return null;
	}

//	public static InputStream getConfigFileInputStream(String configFilePath)
//			throws IOException {
//		return getConfigFileInputStream(configFilePath, true);
//	}

	public static Map<String,Object> loadYaml(String confPath) {
		return findAndReadYaml(confPath, true, true);
	}

}
