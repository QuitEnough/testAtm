package ru.yana;

public class Main {
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.loadCash(50, 1000);
        atm.loadCash(100, 1000);
        atm.loadCash(500, 1000);
        atm.loadCash(1000, 1000);
        atm.loadCash(50000, 1000);
        atm.getCash(5100);
        System.out.println(atm.allAtmSum());
    }
}