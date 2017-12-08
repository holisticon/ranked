package de.holisticon.ranked.extension

import javax.validation.Validation
import javax.validation.Validator

fun Any.validate() = this.validate(Validation.buildDefaultValidatorFactory().validator)

fun Any.validate(validator: Validator) = validator.validate(this).toList()
