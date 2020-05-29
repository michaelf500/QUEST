/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.quest;

import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class Main {

    static void add_three(ArrayList A) {
        A.add(3);
    }
    public static void main(String[] args) {

        QuestPlus qpA = new QuestPlus();
        qpA.testQuest();
        
        ArrayList a = new ArrayList(6);
        System.out.println("three?" + a);
        add_three(a);
        System.out.println("three?" + a);
    }
}
