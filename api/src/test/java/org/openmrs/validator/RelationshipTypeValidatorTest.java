/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.RelationshipType;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 *Tests methods on the {@link RelationshipTypeValidator} class.
 *
 * @since 1.10
 */
public class RelationshipTypeValidatorTest {
	
	/**
	 * @see RelationshipTypeValidator#validate(Object,Errors)
	 * @verifies fail validation if aIsToB(or A is To B) is null or empty or whitespace
	 */
	@Test
	public void validate_shouldFailValidationIfaIsToBIsNullOrEmptyOrWhitespace() throws Exception {
		RelationshipType type = new RelationshipType();
		
		Errors errors = new BindException(type, "type");
		new RelationshipTypeValidator().validate(type, errors);
		Assert.assertTrue(errors.hasFieldErrors("aIsToB"));
		
		type.setaIsToB("");
		errors = new BindException(type, "type");
		new RelationshipTypeValidator().validate(type, errors);
		Assert.assertTrue(errors.hasFieldErrors("aIsToB"));
		
		type.setaIsToB(" ");
		errors = new BindException(type, "type");
		new RelationshipTypeValidator().validate(type, errors);
		Assert.assertTrue(errors.hasFieldErrors("aIsToB"));
	}
	
	/**
	 * @see RelationshipTypeValidator#validate(Object,Errors)
	 * @verifies fail validation if bIsToA(or B is To A) is null or empty or whitespace
	 */
	@Test
	public void validate_shouldFailValidationIfbIsToAIsNullOrEmptyOrWhitespace() throws Exception {
		RelationshipType type = new RelationshipType();
		
		Errors errors = new BindException(type, "type");
		new RelationshipTypeValidator().validate(type, errors);
		Assert.assertTrue(errors.hasFieldErrors("bIsToA"));
		
		type.setbIsToA("");
		errors = new BindException(type, "type");
		new RelationshipTypeValidator().validate(type, errors);
		Assert.assertTrue(errors.hasFieldErrors("bIsToA"));
		
		type.setbIsToA(" ");
		errors = new BindException(type, "type");
		new RelationshipTypeValidator().validate(type, errors);
		Assert.assertTrue(errors.hasFieldErrors("bIsToA"));
	}
	
	/**
	 * Test for all the field being set to some values
	 * @see RelationshipTypeValidator#validate(Object,Errors)
	 * @verifies pass validation if all required fields are set
	 */
	@Test
	public void validate_shouldPassValidationIfAllRequiredFieldsAreSet() throws Exception {
		RelationshipType type = new RelationshipType();
		type.setaIsToB("A is To B");
		type.setbIsToA("B is To A");
		
		Errors errors = new BindException(type, "type");
		new RelationshipTypeValidator().validate(type, errors);
		Assert.assertFalse(errors.hasErrors());
	}
}
