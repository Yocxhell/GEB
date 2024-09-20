/*
 * Classe utilizzata per input da TASTIERA con controllo di errore incluso
 */

package com.yocxhell.robottest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Yocxhell
 */

public class CatchInput {
    
    private InputStreamReader input = new  InputStreamReader (System.in);
    private BufferedReader Keyb = new BufferedReader (input);
    
    
    public int intValue (String text) {
        boolean f = false;
        int x = 0;
        
        do {try {System.out.print (text);
                 String nl = Keyb.readLine();
                 x = Integer.parseInt(nl);
                }
            catch (Exception e) {
                   continue;
                                } 
            f = true;
           }
        while (!f);
        
        return x;
                                      }
    
    
    public float floatValue (String text) {
        boolean f = false;
        float x = 0;
        
        do {try {System.out.print (text);
                 String nl = Keyb.readLine();
                 x = Integer.valueOf(nl).floatValue();
                }
            catch (Exception e) {
                   continue;
                                }
            f = true;
           }
        while (!f);
        
        return x;
                                          }
    
    
    public double doubleValue (String text) {
        boolean f = false;
        double x = 0;
        
        do {try {System.out.print (text);
                 String nl = Keyb.readLine();
                 x = Integer.valueOf(nl).doubleValue();
                }
            catch (Exception e) {
                   continue;
                                }
            f = true;
           }
        while (!f);
        
        return x;
                                            }
    
    
    public long longValue (String text) {
        boolean f = false;
        long x = 0;
        
        do {try {System.out.print (text);
                 String nl = Keyb.readLine();
                 x = Integer.valueOf(nl).longValue();
                }
            catch (Exception e) {
                   continue;
                                }
            f = true;
           }
        while (!f);
        
        return x;
                                        }
    
    
    public String stringValue (String text) {
        boolean f = false;
        String x = null;
        
        do {try {System.out.print (text);
                 x = Keyb.readLine();   
                }
            catch (Exception e) {
                   continue;
                                }
            f = true;
           }
        while (!f);
        
        return x;
                                            }
    
                        }
