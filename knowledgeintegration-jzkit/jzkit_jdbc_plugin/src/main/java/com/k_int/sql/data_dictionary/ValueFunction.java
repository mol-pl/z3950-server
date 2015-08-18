package com.k_int.sql.data_dictionary;

/**
 * Title:       ValueFunction
 * @version:    $Id: ValueFunction.java,v 1.1 2004/10/22 09:24:28 ibbo Exp $
 * Copyright:   
 * @author:     Ian Ibbotson
 * Company:     
 * Description:
 */
public class ValueFunction
{
    private String function_name;

    public ValueFunction(String func)
    {
        function_name = func;
    }

    public String getFunctionName()
    {
        return function_name;
    }
}
