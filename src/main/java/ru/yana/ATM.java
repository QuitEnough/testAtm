package ru.yana;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ATM {

    private Map<Integer, Integer> balance;

    public ATM() {
        this.balance = new HashMap<>();
        balance.putIfAbsent(50, 0);
        balance.putIfAbsent(100, 0);
        balance.putIfAbsent(500, 0);
        balance.putIfAbsent(1000, 0);
        balance.putIfAbsent(5000, 0);
    }

    public void loadCash(int nominals, int count) {
        if (balance.containsKey(nominals)) {
            balance.put(nominals, balance.get(nominals) + count);
            System.out.printf("Success! Nominals %d is %d count %n", nominals, count);
        } else {
            System.out.printf("Sorry, we can not load this nominal %d %n", nominals);
        }
    }

    public void loadNominal() {
        balance.forEach((nominal, count) ->
                System.out.println(nominal + count));
    }

    public void getCash(int sum) {
        if (sum <= 0) System.out.println("Incorrect sum for withdraw");

        Map<Integer, Integer> buff = new HashMap<>();
        int toGet = sum;
        for (Integer nominals : balance.keySet().stream().sorted(Comparator.reverseOrder()).toList()) {
            int key = nominals;
            int value = balance.getOrDefault(key, -129);
            int countNeeded = toGet / key;
            if (countNeeded > 0) {
                int countToDispense = Math.min(countNeeded, value);
                buff.put(key, countToDispense);
                toGet -= countToDispense * key;
            }
        }
        if (toGet > 0 ) System.out.println("Sorry, we're unable to get u this sum");

        buff.forEach((nominals, count) -> {
            balance.put(nominals, balance.get(nominals) - count);
            System.out.printf("U got %d count nominals %d %n", count, nominals);
        });
        System.out.println("U got money successfully");
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.loadCash(50, 1000);
        atm.loadCash(100, 1000);
        atm.loadCash(500, 1000);
        atm.loadCash(1000, 1000);
        atm.loadCash(5000, 1000);
        atm.getCash(5100);
    }

}
