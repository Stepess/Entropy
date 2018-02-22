package entropy;

import java.io.*;
import java.util.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//package entropy;

/**
 *
 * @author Степан
 */
public class Entropy {

    /**
     * @param args the command line arguments
     */
    static String text="";
    static final String PATH = "D:\\\\Programming\\Crypt\\entropy\\text\\input.txt";
    public static void main(String[] args) throws Exception {
        
        try{
            text=Reader.readFile(PATH);
        }
        catch(IOException ex){
            System.out.print(ex.getMessage());
        }
        int[] count = new int[32];
        for (char c: text.toCharArray()){
            count[((int)c - 1072)] +=1; 
        }   
        for (int i: count){
            System.out.println(i);
        }
    }
    
}

class Reader {
    public static String readFile(String path) throws IOException{
        String res = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"CP1251"));
        StringBuilder sb = new StringBuilder();
        String s;
        while((s = br.readLine()) != null) {
            s=s.toLowerCase();
            //s=s.replaceAll("\\w+", "");
            s=s.replaceAll("\\w+|\\!|\\,|\\.|\\?|\\s", "");
            sb.append(s);
        }
        res = sb.toString();
        //System.out.println(s);
        br.close();
        
        
        return res;
    }
}


