package entropy;
import java.io.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Entropy {
    private static final int SIZE_WT_SPACE = 32;//длина алфавита 33 с пробелом
    private static final int SIZE_W_SPACE = 33;
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
        return res/2;
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
    
    static float[][] convertToFreq(int[][] arr, int size){
        float[][] res = new float[size][size];
        int len = 0;
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                len += arr[i][j];
            }
        }
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                res[i][j] = (float)(arr[i][j]*100)/len;
            }
        }
        return res;
    }
    
    static float[] printResult(int SIZE, int len, int[] count, int[][] arrBigramWt, int[][] arrBigramW){
        float[] res = new float[3];
        DoubleArray mono = new DoubleArray(SIZE);
        mono.setValue(count);
        mono.sort();
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
        res[0] = calcEntropy(mono.getArrFreq(),SIZE);
        System.out.println(res[0]);
        System.out.println("===============================");
        System.out.println("Bigrams without intersection");
        System.out.println("===============================");
        /*int calc = 0;
        for(int i=0;i<32;i++){
            calc += arrBigramW[i][32];
        }
        for(int i=0;i<32;i++){
            calc += arrBigramW[32][i];
        }
        len = len - calc;*/
        float[][] freqBigramWt = convertToFreq(arrBigramWt,SIZE);
        printTable(arrBigramWt, SIZE);
        System.out.println();
        printTable(freqBigramWt,SIZE);
        System.out.println("===============================");
        System.out.println("Entropy:");
        System.out.println("===============================");
        res[1] = calcEntropy(freqBigramWt,SIZE);
        System.out.println(res[1]);
        System.out.println("===============================");
        System.out.println("Bigrams with intersection");
        System.out.println("===============================");
        printTable(arrBigramW,SIZE);
        float[][] freqBigramW = convertToFreq(arrBigramW,SIZE);
        printTable(freqBigramW, SIZE);
        System.out.println();
        System.out.println("===============================");
        System.out.println("Entropy:");
        System.out.println("===============================");
        res[2] = calcEntropy(freqBigramW,SIZE);
        System.out.println(res[2]);
        return res;
    }
    
    static final String PATH = "D:\\\\Programming\\Crypt\\entropy\\text\\master.txt";
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
        int[][] arrBigramWt = reader.getArrBigramWt();
        int[][] arrBigramW = reader.getArrBigramW();
        float[] wt_Space = printResult(SIZE_WT_SPACE, len - count[32], count, arrBigramWt, arrBigramW);
        float[] w_Space = printResult(SIZE_W_SPACE, len, count, arrBigramWt, arrBigramW);
        System.out.println();
        for (int i=0;i<3;i++){
            System.out.println(wt_Space[i] + " vs " + w_Space[i]);
        }
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
        while((s = br.readLine()) != null) {
            sb.append(s);
        }
            s=sb.toString();
            sb.setLength(0);
            s=s.replaceAll("\\ё", "е");  
            
            //s=s.replaceAll("[^А-Яа-я]|\n", "");
            s=s.replaceAll("\\s+", " ");
            s=s.replaceAll("\\w+|\\!|\\,|\\.|\\?|\\*|\\-|\\(|\\)|\\\"|\\:|\\^|\\#|\\$|\\%|\\_|\\\n|\\;|\\/|\\<|\\>|\\…|\\—|\\@", "");
            s=s.toLowerCase();
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
