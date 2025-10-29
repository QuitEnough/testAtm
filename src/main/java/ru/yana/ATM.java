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
            balance.putIfAbsent(nominals, balance.get(nominals) + count);
            System.out.println("Success! You have money");
        } else {
            System.out.println("No money, it's mine");
        }
    }

    public void loadNominal() {
        balance.forEach((nominal, count) ->
                System.out.println(nominal + count));
    }

    public void getCash(int sum) {
        Map<Integer, Integer> buff = new HashMap<>();
        int toGet = sum;
        for (Integer key : balance.keySet().stream().sorted(Comparator.reverseOrder()).toList()) {
            int count = 0;
            buff.putIfAbsent(key, 0);
            if (toGet - balance.get(key) > 0) {
                count = sum / key;
                toGet = sum % key;
                buff.put(key, count);
                System.out.println("выдано: " + sum + ", номиналом " + key + ", количество " + buff.get(key));
            }
        }
    }


}
