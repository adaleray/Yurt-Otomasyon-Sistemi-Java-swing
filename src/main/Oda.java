package main;
//Oda sınıfı
public class Oda {
 private int numara;
 private int kapasite;

 public Oda(int numara, int kapasite) {
     this.numara = numara;
     this.kapasite = kapasite;
 }

 public int getNumara() {
     return numara;
 }

 public int getKapasite() {
     return kapasite;
 }

 @Override
 public String toString() {
     return String.valueOf(numara);
 }
}
