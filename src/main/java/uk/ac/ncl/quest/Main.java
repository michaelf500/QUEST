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

        ArrayList a = linspace(-8,8,30);//(-2,2,5);//(-5,5,10);//
        ArrayList b = linspace(.01,3,5);//(3,5,3);
        ArrayList c = linspace(1,1,1);
        ArrayList d = linspace(0,0,1);
        ArrayList paramD = new ArrayList();
        paramD.add(a);
//        paramD.add(b);
//        paramD.add(c);
//        paramD.add(d);
        ArrayList stimD =linspace(-10,10,50);//(-8,8,30);//(0,40,30);//
        double[] fixedp= {Double.NaN,1,0,0};
//        double[] fixedp= {Double.NaN,Double.NaN,0,0.5};
        QuestPlus qpA = new QuestPlus(QuestPlus.GAUSSIAN_MODEL,stimD,paramD,2.5,fixedp);
        //qpA.printList(qpA.paramDomain);
        System.out.println("Posterior: ");
        qpA.printArray(qpA.posterior);
        qpA.printList(qpA.getTargetStim());
        System.err.println("entropy: "+qpA.stdev());
        qpA.update(25, false);
        System.err.println("entropy: "+qpA.stdev());
        qpA.printArray(qpA.posterior);
        qpA.printList(qpA.getTargetStim());

        qpA.update(35, false);
        System.err.println("entropy: "+qpA.stdev());
        qpA.printArray(qpA.posterior);
//        System.out.println("Target Stim: ");
//        qpA.printList(qpA.getTargetStim());
//        System.out.println("----- ");
//        System.out.println("Size param: " + qpA.paramDomain.size());
        
        
        /* Not sure the calculated likelihoods match. 2021-01-08
        The EH look to have different lengths?
        At last count, with the correct function (note fixedp[3] = g = 0)
        the EH on 1D matched at first pass.
        Only changes to QuestPlus since last commit are print statements.
        
        Update 2021-01-12
        The getTargetStim, update, entropy and stdev functions all seem
        to produce the same as the matlab for the 1D case.

*/
        
        
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
