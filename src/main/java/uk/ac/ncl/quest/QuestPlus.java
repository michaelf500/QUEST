/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.quest;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.Arrays;
import java.util.*;

/**
 * This is supposed to implement (for limited cases) the QUEST+ algorithm.
 * Adapted from
 * https://openresearchsoftware.metajnl.com/articles/10.5334/jors.195/
 *
 * @author Michael
 */
public class QuestPlus {

    static final int GAUSSIAN_MODEL = 1;

    static final int ENTROPY = 1;
    static final int NTRIALS = 2;
    static final int STDEV = 3;

    static final int STIM_MIN = 1;
    static final int STIM_WEIGHTED = 2;
    static final int STIM_PERCENT = 3;
    static final int STIM_MIN_OR_RAND = 4;
    int F = GAUSSIAN_MODEL;
    CDF vF;
    List paramDomain; //vector of possible stimulus values.
    /* vector/ matrix of possible values (mu, sigma etc
	for the function F. */
    List stimDomain;
    boolean respDomain; //possible responses. I'm only considering correct/wrong.
    int stopRule = ENTROPY;
    float stopCriterion = 3; // no. of trial or entropy. 
    int minNTrials = 0;
    int maxNTrials = 30000;

    int stimSelectionMethod = STIM_MIN;
    int stimSelectionParam = 2;
    int stimConstrainToNOfPrev[] = {}; //TODO
    float[][] prior; //containing probability of each parameter-combination
    float[][] likelihoods;
    /*2D matrix, containing conditional probabilities 
	* of each outcome at each stimulus-combination/parameter-combination*/
    float[][] posterior;
    List<Double> historyStim;
    List<Boolean> historyResp;

    QuestPlus() {
    }

    QuestPlus(int F, ArrayList stimD, ArrayList paramD,  double stopC) {
        /*
            * stimD needs to be an ArrayList containing at least one ArrayList
		 * So this needs to
		 * a) create param domain by combining all the possible parameter values
		 * b) ditto for priors
         */
        if (F == QuestPlus.GAUSSIAN_MODEL) {
            vF = new NormCDF();
        } else {
            return;
        }
        //TODO: add check paramD is ArrayList
        if (paramD.size() ==1 ){
            paramDomain = paramD;
        } else { //make combination matrix
            paramDomain = cartesianProduct(paramD); 
            make2D(paramDomain);
        }
        paramDomain = paramD;
    
    }
    void printParamDomain() {
        ListIterator iter = paramDomain.listIterator();
        
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
    void testQuest() {

        List x = new ArrayList(Arrays.asList(10,11));
        List y = new ArrayList(Arrays.asList(13,14));
        List z = new ArrayList(Arrays.asList(16,17));
        List a = new ArrayList(Arrays.asList(x,y,z));
        List b = new ArrayList(Arrays.asList(21,22,23,24));
        List c = new ArrayList(Arrays.asList(31,32,33,34));
        List d = new ArrayList(Arrays.asList(b,c));
        List conc = cartesianProduct(Arrays.asList(a, d));
        
        make2D(conc);
            System.out.println(conc);

    }

    private static void make2D(List conc) {
        /* so the point of the next convoluted bit
        * is to convert into an Array of Array, because otherwise
        * so [ [[1,2,3],4], ...  becomes [ [1,2,3,4], ...
        * Only is expecting lists of lists as above line, so if there are 
        * too many, they simply don't get concatenatied.
        * Also is assuming that it is being passed at minium a list of lists.
        */
        ListIterator iter = conc.listIterator();
        
        while (iter.hasNext()) {
            List tmpL = new ArrayList();
            Object eEl= iter.next();
            if (eEl instanceof java.util.ArrayList) { // if not, not sure what to do...
                ListIterator iter2 = ((java.util.ArrayList)eEl).listIterator();
                while (iter2.hasNext()) {
                Object elment = iter2.next();
                if (elment instanceof java.util.ArrayList) {
                    Iterator<List> iter3 = ((java.util.ArrayList)elment).iterator();
                    while (iter3.hasNext()) {                      
                        tmpL.add(iter3.next());
                    }
                } else {
                    tmpL.add(elment);
                }
            }
                iter.set(tmpL);                   
            }
        }
    }
    private static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        // stolen from https://stackoverflow.com/questions/714108/cartesian-product-of-arbitrary-sets-in-java/10083452#10083452
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
//                    resultList.addAll(remainingList);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
}
