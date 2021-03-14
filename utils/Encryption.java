package socialnetwork.utils;

import java.util.Arrays;

public class Encryption {
    public static String encrypt(int key,String data){
        char[] rez=data.toCharArray();
        for(int i=0;i<data.length();i++){
            if(i%2==0){
                rez[i]+=i+key;
            }else{
                rez[i]-=(i+key);
            }
        }
        String s="";
        for(char c:rez){
            s+=c;
        }
        return s;
    }
    public static String decrypt(int key,String data){
        char[] rez=data.toCharArray();
        for(int i=0;i<data.length();i++){
            if(i%2!=0){
                rez[i]+=i+key;
            }else{
                rez[i]-=(i+key);
            }
        }
        String s="";
        for(char c:rez){
            s+=c;
        }
        return s;
    }
}
