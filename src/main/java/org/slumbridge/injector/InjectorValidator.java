/*
 * Copyright (c) 2019, Lucas <https://github.com/Lucwousin>
 * All rights reserved.
 *
 * This code is licensed under GPL3, see the complete license in
 * the LICENSE file in the root directory of this submodule.
 */
package org.slumbridge.injector;

import org.slumbridge.injector.injection.InjectData;
import org.slumbridge.injector.rsapi.RSApi;
import static org.slumbridge.injector.rsapi.RSApi.API_BASE;
import org.slumbridge.injector.rsapi.RSApiClass;
import org.slumbridge.injector.rsapi.RSApiMethod;
import lombok.RequiredArgsConstructor;
import net.runelite.asm.ClassFile;
import net.runelite.asm.pool.Class;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

@RequiredArgsConstructor
public class InjectorValidator implements Validator
{
	private static final Logger log = Logging.getLogger(InjectorValidator.class);
	private static final String OK = "OK", ERROR = "ERROR", WTF = "WTF";
	private final InjectData inject;

	private int missing = 0, okay = 0, wtf = 0;

	public boolean validate()
	{
		final RSApi rsApi = inject.getRsApi();
		for (ClassFile cf : inject.getVanilla())
		{
			for (Class intf : cf.getInterfaces())
			{
				if (!intf.getName().startsWith(API_BASE))
				{
					continue;
				}

				RSApiClass apiC = rsApi.findClass(intf.getName());
				if (apiC == null)
				{
					log.error("{} is rs api type implemented by {} but it doesn't exist in rsapi. wtf", intf, cf.getPoolClass());
					++wtf;
					continue;
				}

				check(cf, apiC);
			}
		}

		String status = wtf > 0 ? WTF : missing > 0 ? ERROR : OK;
		log.info("[INFO] RSApiValidator completed. Status [{}] {} overridden methods, {} missing", status, okay, missing);

		// valid, ref to static final field
		return status == OK;
	}

	private void check(ClassFile clazz, RSApiClass apiClass)
	{
		for (RSApiMethod apiMethod : apiClass)
		{
			if (apiMethod.isSynthetic() || apiMethod.isDefault())
			{
				continue;
			}

			if (clazz.findMethodDeep(apiMethod.getName(), apiMethod.getSignature()) == null)
			{
				System.out.println("Method name: "+apiMethod.getName());
				log.error("[WARN] Class {} implements interface {} but doesn't implement {}",
						clazz.getPoolClass(), apiClass.getClazz(), apiMethod.getMethod());
				++missing;
			}
			else
			{
				++okay;
			}
		}
	}
}
