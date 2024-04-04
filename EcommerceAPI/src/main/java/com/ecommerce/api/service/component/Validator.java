package com.ecommerce.api.service.component;

import org.springframework.stereotype.Component;

@Component
public class Validator {
    public boolean validate(String CPF){
        int[] CPF_Array = CPFtoArray(CPF);
        return this.verifyCode(1, CPF_Array);
    }

    private int[] CPFtoArray(String CPF){
        int[] CPF_Array = new int[11];
        for(int i=0; i<=10; i++)
            CPF_Array[i] = Integer.parseInt(String.valueOf(CPF.charAt(i)));
        return CPF_Array;
    }

    private boolean verifyCode(int position, int[] CPF_Array){
        int multiplier;
        if(position == 1)
            multiplier = 10;
        else
            multiplier = 11;

        int index = 7 + position;
        int result = 0;
        for(int i = 0; i<= index; i++){
            result += multiplier * CPF_Array[i];
            multiplier--;
        }

        int rest = result % 11;
        if(rest < 2) {
            if(CPF_Array[index + 1] == 0) {
                if(position == 1)
                    return verifyCode(2, CPF_Array);
                else
                    return true;
            } else
                return false;
        } else {
            int difference = 11 - rest;
            if(CPF_Array[index + 1] == difference) {
                if(position == 1)
                    return verifyCode(2, CPF_Array);
                else
                    return true;
            } else
                return false;
        }
    }
}
