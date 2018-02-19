package entropy;

import java.io.*;
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
    static final String PATH = "D:\\\\Programming\\Crypt\\entropy\\text\\input.txt";
    public static void main(String[] args) throws Exception {
        try{
            Reader.readFile(PATH);
        }
        catch(IOException ex){
            System.out.print(ex.getMessage());
        }
    }
    
}

class Reader {
    public static String readFile(String path) throws Exception{
        String res = "";
        try(FileReader reader = new FileReader(path))
        {
            int c;
            while((c=reader.read())!=-1){
                System.out.print((char)c);
            }
            reader.close();
        }
        catch(IOException ex){
            System.out.print(ex.getMessage());
        }
        return res;
    }
}