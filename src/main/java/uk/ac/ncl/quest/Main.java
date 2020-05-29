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

    static ArrayList linspace(double start,double end, int n) {
        /**
         * Creates an ArrayList of n values between start and end (inclusively)
         */
        ArrayList output = new ArrayList(n);
        for (int i =0; i<n; i++) {
            output.add(start+(end-start)/(n-1)*i);
        }
        return output;
    }
    public static void main(String[] args) {

        ArrayList a = linspace(-10,10,50);
        ArrayList b = linspace(-10,10,50);
        ArrayList paramD = new ArrayList();
        paramD.add(a);
        paramD.add(b);
        ArrayList stimD =linspace(-8,8,30);
        
        
        QuestPlus qpA = new QuestPlus(QuestPlus.GAUSSIAN_MODEL,stimD,paramD,2.5);
        qpA.printParamDomain();
//        qpA.testQuest();
//        
//        ArrayList a = new ArrayList(6);
//        System.out.println("three?" + a);
//        add_three(a);
//        System.out.println("three?" + a);
    }
}
