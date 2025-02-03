package com.project;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.ArrayList;

public class TestApp {
    @Test
    public void test() {
        String[] s = new String[]{"我", "是", "你", "爸爸"};
        for (String str : s) {
            System.out.print(str + " ");
        }
        System.out.println();
        Gson gson = new Gson();
        String json = gson.toJson(s);
        System.out.println(json);

        ArrayList arrayList = gson.fromJson(json, ArrayList.class);
        System.out.println(arrayList);
    }
}
