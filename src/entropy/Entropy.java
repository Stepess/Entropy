package entropy;
import java.io.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Entropy {
    private static final int SIZE = 32;//длина алфавита 33 с пробелом
    static float calcEntropy(float[] freq,int size){
        float res=0;
        for(int i=0;i<size;i++){
            res = res - (freq[i]/100)*(float)((Math.log(freq[i]/100))/Math.log(2));
        }
        return res;
    }
    static float calcEntropy(float[][] freq,int size){
        float res=0;
        for(int i=0;i<size;i++)
            for(int j=0;j<size;j++){
                if (freq[i][j]!=0.0)//иначе вылазит NaN
                    res = res - (freq[i][j]/100)*(float)((Math.log(freq[i][j]/100))/Math.log(2));
            }
        return res;
    }
    //Сначала строка потом столбец, т.е. сочитание "ва" 2 строка, 0 столбец
    static void printTable(int[][] count, int size){
        System.out.print("  ");
        for(int i=0; i<size; i++){
            System.out.printf("%7s",(char)(i +1072));
            if (i==32)
                System.out.printf("%7s",' ');
        }   
        System.out.println();
        for(int j=0;j<size;j++){
            System.out.print((char)(j +1072) + "  ");
            for(int i=0;i<size;i++)
                System.out.printf("%7d", count[j][i]);
            System.out.println();
            if (j==32)
                System.out.print("   ");
        }
    }
   
    static void printTable(float[][] count, int size){
        System.out.print("");
        for(int i=0; i<size; i++){
            System.out.printf("%8s",(char)(i +1072));
            if (i==32)
                System.out.printf("%5s",' ');
        }  
        System.out.println();
        for(int j=0;j<size;j++){
            System.out.print((char)(j +1072) + "  ");
            for(int i=0;i<size;i++)
                System.out.printf("%7.4f%%", count[j][i]);
            System.out.println();
            if (j==32)
                System.out.print("   ");
        }
    }
    
    static float[][] convertToFreq(int[][] arr, int size,int len){
        float[][] res = new float[size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                res[i][j] = (float)(arr[i][j]*100)/len;
            }
        }
        return res;
    }
    static final String PATH = "D:\\\\Programming\\Crypt\\entropy\\text\\lordring.txt";
    public static void main(String[] args) throws Exception {
        Reader reader = new Reader();
        int len=0;
        try{
            reader.readFile(PATH);
            len=reader.getLen();
        }
        catch(IOException ex){
            System.out.print(ex.getMessage());
        }
        int[] count = reader.getArrMono();
        DoubleArray mono = new DoubleArray(SIZE);
        mono.setValue(count);
        mono.sort();
        if (SIZE==32)
            len = len - count[32];
        System.out.println("===============================");
        System.out.println("Number of symbol in text:");
        System.out.println("===============================");
        System.out.println(mono);
        System.out.println("===============================");
        System.out.println("Sorted list of frequency of symbols:");
        System.out.println("===============================");
        mono.convert(len);
        mono.printNotZeroFreq();
        System.out.println("===============================");
        System.out.println("Entropy:");
        System.out.println("===============================");
        System.out.println(calcEntropy(mono.getArrFreq(),SIZE));
        System.out.println("===============================");
        System.out.println("Bigrams without intersection");
        System.out.println("===============================");
        int[][] arrBigramWt = reader.getArrBigramWt();
        float[][] freqBigramWt = convertToFreq(arrBigramWt,SIZE,len/2);
        printTable(arrBigramWt, SIZE);
        System.out.println();
        printTable(freqBigramWt,SIZE);
        System.out.println("===============================");
        System.out.println("Entropy:");
        System.out.println("===============================");
        System.out.println(calcEntropy(freqBigramWt,SIZE));
        int[][] arrBigramW = reader.getArrBigramW();
        System.out.println("===============================");
        System.out.println("Bigrams with intersection");
        System.out.println("===============================");
        printTable(arrBigramW,SIZE);
        float[][] freqBigramW = convertToFreq(arrBigramW,SIZE,len-1);
        printTable(freqBigramW, SIZE);
        System.out.println();
        System.out.println("===============================");
        System.out.println("Entropy:");
        System.out.println("===============================");
        System.out.println(calcEntropy(freqBigramW,SIZE));
        }
}

class Reader {
    private int len;
    int[] countMono = new int[33];
    int[] [] countBigram = new int[33][33];
    int[] [] countBigramW = new int[33][33];
    public int index(char c){
        if ((int)(c)==32)
            return 32;
        else
            return (int)c - 1072;
    }
    public void readFile(String path) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"CP1251"));
        String s;
        StringBuilder sb = new StringBuilder();
        int k=0;
        while((s = br.readLine()) != null) {
            sb.append(s);
            k++;
            if (k==13){
                s=sb.toString();
                sb.setLength(0);
                //System.err.println((char)64);
                s=s.replaceAll("\\w+|\\!|\\,|\\.|\\?|\\*|\\-|\\(|\\)|\\\"|\\:|\\^|\\#|\\$|\\%|\\ё|\\_|\\\n|\\;|\\/|\\<|\\>|\\…|\\—|\\@", "");
                s=s.replaceAll("\\s+", " ");
                s=s.toLowerCase();
                //System.out.println(s);
                len +=  s.length();
                char[] arr = s.toCharArray();
                for (int i=0;i<s.length();i++){
                    if (i%2!=0){  
                        countBigram[index(arr[i-1])][index(arr[i])] +=1;
                    }
                    if(i!=0){
                        countBigramW[index(arr[i-1])][index(arr[i])] +=1;
                    }
                    countMono[index(arr[i])] +=1;
                }
                k=0;
            }  
        }
        if(k!=0){
                s=sb.toString();
                sb.setLength(0);
                s=s.replaceAll("\\w+|\\!|\\,|\\.|\\?|\\*|\\-|\\(|\\)|\\\"|\\:|\\^|\\#|\\$|\\%|\\ё|\\_|\\\n|\\;|\\/|\\<|\\>|\\…|\\—", "");;
                s=s.replaceAll("\\s+", " ");
                s=s.toLowerCase();
                //System.out.println(s);
                len +=  s.length();
                char[] arr = s.toCharArray();
                for (int i=0;i<s.length();i++){
                    if (i%2!=0){  
                        countBigram[index(arr[i-1])][index(arr[i])] +=1;
                    }
                    if(i!=0){
                        countBigram[index(arr[i-1])][index(arr[i])] +=1;
                    }
                    countMono[index(arr[i])] +=1;
                }
                k=0;
        }
        System.out.println();
        br.close();
    }
    public int[] getArrMono(){
        return countMono;
    }
    public int[][] getArrBigramWt(){
        return countBigram;
    }
    public int[][] getArrBigramW() {
        return countBigramW;
    }
    public int getLen(){
        return len;
    }
}

class DoubleArray {
    private int size;
    private int[] value = new int[size];
    private float[] freq = new float[size];
    private char[] order = new char[size];
    DoubleArray(int size){
        this.size=size;
        value = new int[size];
        freq = new float[size];
        this.order = new char[size];
        for(int i=0;i<size;i++){
            order[i]=(char)(i+1072); 
        }
        if (size==33)
            order[32] = ' ';
    }
    public void setValue(int[] arr){
        for(int i=0; i<size;i++){
            value[i] = arr[i];
        }        
    }
    @Override
    public String toString(){
        String res="";
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<size;i++){
            res= res+order[i];
            res+=" = ";
            res+=value[i] + "\n";
        }
        return res;
    }
    public void convert(int len){
        for(int i=0;i<size;i++)
            freq[i] = (float)(value[i]*100)/len;
    }
    public int searchMax(int from, int to){
        int max = value[from];
        int res=from;
        for(int i=from+1;i<to;i++){
            if(value[i]>max){
                max=value[i];
                res = i;
            }
        }
        return res;
    }
    public void swap(int x, int y){
        int buf;
        char ch;
        buf=value[x];
        value[x] = value[y];
        value[y] = buf;
        ch=order[x];
        order[x] = order[y];
        order[y] = ch;
    }
    public void sort(){
        for(int i=0;i<size;i++){
            this.swap(i, this.searchMax(i, size));
        }
    }
    public void printNotZeroFreq(){
        int i=0;
        while((value[i]!=0)&&(i<size)){
            System.out.printf("%s = %f%%%n ",order[i],freq[i]);
            i++;
            if(i==32) break;
        }
    }
    public float[] getArrFreq(){
        return freq;
    }
}

/*
public void printNotZero(int len){
        int i=0;
        double d =0;
        while((value[i]!=0)&&(i<size)){
            d = (double)(value[i]*100)/len;
            //System.out.println(order[i]+" = " + d +"%");
            System.out.printf("%s = %f%%%n ",order[i],d);
            i++;
            if(i==32) break;
        }
    }

class Reader {
    public static String readFileMono(String path) throws IOException{
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
*/
//s=s.replaceAll("[^А-Яа-я]|\n", " ");