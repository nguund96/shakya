package dn.ute.shakya.common;

public class Format {

    public static String formatWord(String content){
        content = content.trim().toLowerCase();
        if(content.length() == 0) return "";
        if(content.length() == 1) return content.toUpperCase();
        content = content.substring(0,1).toUpperCase() + content.substring(1);
        return content;
    }
}
