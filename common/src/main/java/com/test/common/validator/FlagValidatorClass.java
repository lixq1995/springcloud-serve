package com.test.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 状态标记校验器
 */
public class FlagValidatorClass implements ConstraintValidator<FlagValidator,String> {
    private String[] values;
    @Override
    public void initialize(FlagValidator flagValidator) {
        this.values = flagValidator.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        for(int i=0;i<values.length;i++){
            if(values[i].equals(String.valueOf(value))){
                isValid = true;
                break;
            }
        }
        return isValid;
    }
}
