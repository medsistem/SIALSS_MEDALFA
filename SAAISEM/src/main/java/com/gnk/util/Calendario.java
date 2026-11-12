/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.util;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author HP-MEDALFA
 */
public class Calendario {
    
    public static boolean first3WorkDays(Date date){
       Calendar c = Calendar.getInstance();
       c.setTime(date);
       int dayMonth =c.get(Calendar.DAY_OF_MONTH);
       int dayWeek = c.get(Calendar.DAY_OF_WEEK);
       if(dayMonth> 5)
           return false;
       if(dayWeek == 1 || dayWeek ==7)
           return false;
       if((dayMonth == 4) && (dayWeek == Calendar.MONDAY || dayWeek == Calendar.TUESDAY || dayWeek == Calendar.WEDNESDAY))
           return true;
       if(dayMonth == 5 && (dayWeek == Calendar.MONDAY || dayWeek == Calendar.TUESDAY || dayWeek == Calendar.WEDNESDAY) )
           return true;
       return dayMonth <= 3 && dayWeek > Calendar.SUNDAY && dayWeek < Calendar.SATURDAY;
    }
    
    public static boolean first3WorkDays(){
        return Calendario.first3WorkDays(new Date());
    }
}
