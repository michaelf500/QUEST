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
        if (n==1) {
            output.add(start);
        } else {
            for (int i =0; i<n; i++) {
                output.add(start+(end-start)/(n-1)*i);
            }
        }
        return output;
    }
    public static void main(String[] args) {

        ArrayList a = linspace(-8,8,30);//(-2,2,5);//
        ArrayList b = linspace(3,5,3);
        ArrayList c = linspace(1,1,1);
        ArrayList d = linspace(0,0,1);
        ArrayList paramD = new ArrayList();
        paramD.add(a);
//        paramD.add(b);
//        paramD.add(c);
//        paramD.add(d);
        ArrayList stimD =linspace(-10,10,50);//(-8,8,30);//
        double[] fixedp= {Double.NaN,1,0,0.5};
        QuestPlus qpA = new QuestPlus(QuestPlus.GAUSSIAN_MODEL,stimD,paramD,2.5,fixedp);
        qpA.printList(qpA.paramDomain);
        System.out.println("Target Stim: ");
        qpA.printList(qpA.getTargetStim());
        System.out.println("----- ");
        System.out.println("Size param: " + qpA.paramDomain.size());
        //qpA.printArray(qpA.posterior);
//        qpA.printList(qpA.prior);
//        qpA.testQuest();
//        
//        ArrayList a = new ArrayList(6);
//        System.out.println("three?" + a);
//        add_three(a);
//        System.out.println("three?" + a);
    }
}
