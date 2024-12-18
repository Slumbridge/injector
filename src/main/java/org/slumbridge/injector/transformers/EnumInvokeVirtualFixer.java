package org.slumbridge.injector.transformers;

import org.slumbridge.injector.injection.InjectData;

import net.runelite.asm.ClassFile;
import net.runelite.asm.Method;
import net.runelite.asm.attributes.code.Instruction;
import net.runelite.asm.attributes.code.instructions.InvokeVirtual;

public class EnumInvokeVirtualFixer extends InjectTransformer
{
	private int fixedEnums = 0;
	public EnumInvokeVirtualFixer(InjectData inject)
	{
		super(inject);
	}

	@Override
	void transformImpl()
	{
		inject.forEachPair(this::fixEnumInvokeVirtuals);
	}

	private void fixEnumInvokeVirtuals(ClassFile rsc, ClassFile vanilla)
	{
		if (vanilla.isEnum()) {
			Method valuesMethod = vanilla.findMethod("values");
			if (valuesMethod != null) {
				for (Instruction insn : valuesMethod.getCode().getInstructions()) {
					if (insn instanceof InvokeVirtual) {
						InvokeVirtual invokeVirtual = (InvokeVirtual) insn;
						invokeVirtual.getMethod().getClazz().fixEnum();
						fixedEnums++;
					}
				}
			}
		}
	}
}