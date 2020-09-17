package com.kms.katalon.controller.test;

import org.junit.Assert;
import org.junit.Test;

import com.kms.katalon.controller.EntityController;

public class EntityControllerTest {
	@Test
	public void testCanHandleCommaInEntityName() {
		String validName = EntityController.toValidFileName("....This@....,,,,,is!......,........my?,........name....");
		Assert.assertEquals(validName, "This....,,,,,is......,........my,........name");
	}

	@Test
	public void testCanRemoveInvalidCharactersFromNamme() {
		String validName = EntityController.toValidFileName("....This@ is! my? name....");
		Assert.assertEquals(validName, "This is my name");
	}
}
