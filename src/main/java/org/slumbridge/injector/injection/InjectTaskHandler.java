/*
 * Copyright (c) 2019, Lucas <https://github.com/Lucwousin>
 * All rights reserved.
 *
 * This code is licensed under GPL3, see the complete license in
 * the LICENSE file in the root directory of this source tree.
 */
package org.slumbridge.injector.injection;

import java.io.File;

/**
 * Interface containing all the methods gradle needs to know about
 */
public interface InjectTaskHandler
{
	/**
	 * The actual method that does all the work
	 */
	void inject();

	/**
	 * Call this to save the injected jar to outputJar
	 */
	void save(File outputJar);

	/**
	 * Call this to save the vanilla hash
	 */
	void hash(File outputFile, File vanilla);
}
