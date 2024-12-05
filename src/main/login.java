package main;

public class login {
    private static String username;
    private static String password;
    private static String ad;
    private static String soyad;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        login.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        login.password = password;
    }

    public static String getAd() {
        return ad;
    }

    public static void setAd(String ad) {
        login.ad = ad;
    }

    public static String getSoyad() {
        return soyad;
    }

    public static void setSoyad(String soyad) {
        login.soyad = soyad;
    }
}
