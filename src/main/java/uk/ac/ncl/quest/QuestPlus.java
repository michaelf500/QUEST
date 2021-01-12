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
    /* So I [MJF] am only considering the case of one variable in the 
    * stim domain (ie angle 0 - 90) 
    * so stimDomain is a vector with n values [ie 0,2,4 ... 90]
    * for paramDomain I am considering having ability to 
    * determine (eg) mu and sigma, each of which has a range of 
    * possible values, so for m possibilities for mu and s for sigma,
    * paramDomain would be a m*s by 2 List
    *
    * So I think that what in matlab is a matrix eg
    * 0 1 2 3 4 
    * 8 9 2 3 5
    *
    * ie 2 x 5 matrix (where row is first dimension
    * ends up a a List of 5 [2 item Lists]
    */
    boolean respDomain; //possible responses. I'm only considering correct/wrong.
    int stopRule = ENTROPY; //nb STDEV only works with 1D params
    double stopCriterion = 3; // no. of trial or entropy. 
    int minNTrials = 0;
    int maxNTrials = 30000;
    int nTrialsCompleted=0;

    int stimSelectionMethod = STIM_MIN;
    int stimSelectionParam = 2;
    int stimConstrainToNOfPrev[] = {}; //TODO
    //List prior; //containing probability of each parameter-combination
    double [] prior; // with size as paramDomain, ie m*s
    double[][][] likelihoods; // [stim][param][resp] ie [n][m*s][2]
    /*2D matrix, containing conditional probabilities 
	* of each outcome at each stimulus-combination/parameter-combination*/
    double [] posterior;
    List historyStim = new ArrayList();
    List<Boolean> historyResp = new ArrayList();

    QuestPlus() {
    }

    QuestPlus(int F, ArrayList stimD, ArrayList paramD,  double stopC, double[] params) {
        /*
            * paramD needs to be an ArrayList containing at least one ArrayList
		 * So this needs to
		 * a) create param domain by combining all the possible parameter values
		 * b) ditto for priors
         */
        //TODO: check 
        stopCriterion = stopC;
        if (F == QuestPlus.GAUSSIAN_MODEL) {
            vF = new NormCDF(params);
        } else {
            return;
        }
        stimDomain = stimD;
        ListIterator iter = paramD.listIterator();
        if (vF.getNParams()!=paramD.size()) {
            throw new IllegalArgumentException("NANs must match params to be estimated");    
        }
        if (paramD.size() ==1 ){
            /* rotate to make n lists, rather than 1 list of n*/
            paramDomain = row2Col(paramD);
        } else { //make combination matrix
            paramDomain = cartesianProduct(paramD); 
            make2D(paramDomain);
        }
/* note from here on, is 'initialise' in the original matlab */
        
        //TODO: add check paramD is ArrayList. Should have vF.getNParam members.
        // aslo that stimDomain is an ArrayList
        
        // set up uniform priors.
        List priorTmp = new ArrayList(paramD.size());
        iter = paramD.listIterator();
        while (iter.hasNext()) {
            int nn = ((List)iter.next()).size();
            ArrayList al = new ArrayList(nn);
            for (int i =0; i< nn; i++) {
                al.add(1.0/nn);
            }
            priorTmp.add(al);
        }
        if (priorTmp.size() > 1) {
            priorTmp = cartesianProduct(priorTmp);
            make2D(priorTmp);
        } else {
            priorTmp = row2Col(priorTmp);
        }
//        System.err.println("prior size: "+priorTmp.size());
//        this.printList(priorTmp);
//        System.err.println(" ");
        prior = new double [priorTmp.size()];
        iter = priorTmp.listIterator();
        int ii=0;
        ListIterator iter2;
        // multiply elements together
        double sum=0;
        while (iter.hasNext()) {
            double t = 1;
            ArrayList row = (ArrayList)iter.next();
            iter2 = row.listIterator();
            while (iter2.hasNext()) {
                t*=(double)iter2.next();
            }
            prior[ii] = t;
            ii++;
            sum+=t;
        }


        // normalise by total sum
        for (ii = 0 ;ii<prior.length; ii++) {
            prior[ii]/=sum;
        }



        
        /*
        % if response domain is binary, and function only
                    % provides one output, we'll assume that the the
                    % probability of the first resposne is the complement
                    % of the second
        
                            obj.likelihoods = nan(length(obj.stimDomain), length(obj.paramDomain), length(obj.respDomain));
                    for i = 1:size(obj.stimDomain,2)
                        for j = 1:size(obj.paramDomain,2)
                            x = num2cell([obj.stimDomain(:,i); obj.paramDomain(:,j)]);
                            y = obj.F(x{:});
                            obj.likelihoods(i,j,:) = [1-y; y]; % complement [backwards format versus above???]
                        end
                    end
        */
    
    /* I think want first dim to be the length of all the parameter
       combinations, so mu1,sd1; mu1,sd2; mu2,sd1; mu2,sd2  etc.
       and need to calculate F for every value in stimDomain for each of these 
       combinations. So we need to specify for model that there are N parameters 
      ( 4 four the gauss)
    */    likelihoods = new double [stimDomain.size()][paramDomain.size()][2];
        double[] vals = new double[vF.getNParams()]; 
        ArrayList valsA ;
        iter = paramDomain.listIterator();
        ii=0;
        int jj;
        while (iter.hasNext()) {
            ListIterator iter3 = stimDomain.listIterator();
            iter2= ((ArrayList)iter.next()).listIterator();
            int i=0;
            while (iter2.hasNext()) {
                vals[i++]=(double)iter2.next();
            }
            jj=0;
            while (iter3.hasNext()) {
                double dddd=(double)iter3.next();
                double tmpV = vF.getValue(dddd,vals);
                System.err.println("t "+dddd+" "+vals[0]+" "+tmpV);
                likelihoods[jj][ii][0] = 1-tmpV;
                likelihoods[jj][ii][1] = tmpV;
                jj++;
            }
            
            ii++;
        }
        posterior = new double[prior.length];
        for (ii=0;ii<posterior.length; ii++ ) {
            posterior[ii] = prior[ii];
        }

    }
    List getTargetStim() {
        double[][][] postTimesL = new double [stimDomain.size()][paramDomain.size()][2];

        /*not sure about all this....*/
//        ListIterator iterPost = posterior.listIterator();
//        int ii =0;
        double[][] pk = new double[stimDomain.size()][2];
//        while (iterPost.hasNext()) {
//            double postval = (double) iterPost.next();
        for (int jj=0; jj< stimDomain.size(); jj++) {
//            double postval = posterior[ii];
            for (int kk=0;kk<2;kk++) {
                pk[jj][kk]=0;
                for (int ii=0;ii<posterior.length; ii++ ){
                    postTimesL[jj][ii][kk]= posterior[ii]*likelihoods[jj][ii][kk];
                    pk[jj][kk]+=postTimesL[jj][ii][kk];
                }               
            }
//            ii++;
        }
        System.err.println("ptl");
        printArray(postTimesL);
//         iterPost = posterior.listIterator();
//        ii =0;
        double[][]H = new double[stimDomain.size()][2];
        double[] EH = new double[stimDomain.size()];
        for (int ii=0; ii< stimDomain.size(); ii++) {
            EH[ii]=0;
            double EHsum=0;
            EHsum=0;
            for (int kk=0;kk<2;kk++) {
                H[ii][kk]=0;
                for (int jj=0; jj< paramDomain.size(); jj++) {
                    //double newPost = postTimesL[ii][jj][kk]/pk[ii][kk];
                    postTimesL[ii][jj][kk]/= pk[ii][kk];
                    double tmp = postTimesL[ii][jj][kk]*Math.log(postTimesL[ii][jj][kk]);
                    if (!Double.isNaN(tmp)&&!Double.isInfinite(tmp)) {
                        H[ii][kk]-=tmp;
                    } 
                }
                EHsum+=H[ii][kk]*pk[ii][kk];
            }       
            EH[ii]=EHsum;
//            System.err.println("EHSum"+EHsum);
        }
        // select stimulus. 
        // using just the default 'min' stimSelectionMethod
        double maxv = Double. MAX_VALUE;
        int idx=-1;
        for (int ii=0; ii< EH.length; ii++) {
            System.err.println("EH"+ii+"  "+EH[ii]);
            if (EH[ii] < maxv ) {
                maxv = EH[ii];
                idx=ii;
            }
        }
        List out = new ArrayList(2);
        if (idx >= 0) {
        out.add(0,stimDomain.get(idx));
        out.add(1,idx);}
        // assuming that stimConstrainToNOfPrev is not set.
//            
//        }
        return (out);
    }
    
    void update(int stimIdx,boolean resp) { //TODO ?stim not stimIdx
        int r=0;
        double sum=0;
        if (resp) r=1;
        for (int ii=0;ii<posterior.length;ii++) {
            posterior[ii] = posterior[ii]*likelihoods[stimIdx][ii][r];
            sum+=posterior[ii];
        }
        for (int ii=0;ii<posterior.length;ii++) {
            posterior[ii]/=sum;
        }
        historyStim.add(stimDomain.get(stimIdx));
        historyResp.add(resp);
        nTrialsCompleted++;
    }

    boolean isFinished() {
        
        if (nTrialsCompleted < minNTrials) return false;
        if (nTrialsCompleted > maxNTrials) return true;
        switch (stopRule) {
            
            case STDEV: 
                return stdev() <= stopCriterion;                     
            case ENTROPY:
                return entropy() <= stopCriterion;                     
            case NTRIALS:
                return false;
            default: 
                return false;        
        }
                
        
    }
    double [] getParamEsts() {//TODO
/*        [~,paramIdx] = min(sqrt(mean(
                bsxfun(@minus, obj.paramDomain, 
                   sum(bsxfun(@times, obj.posterior, obj.paramDomain), 2)
                ).^2,1)));*/
        double [] out = new double [paramDomain.size()];
        ListIterator iter = paramDomain.listIterator();
        
        double [][] tmp = new double[paramDomain.size()]
                [((List)paramDomain.get(0)).size()]; 
        int ii=0;
        int jj=0;
        while (iter.hasNext()) {
            jj=0;
            ListIterator iter2= ((ArrayList)iter.next()).listIterator();
            //double v =0;
            while (iter2.hasNext()) {
                tmp[ii][jj] = (double)iter2.next()*posterior[ii];
                jj++;
            }
            ii++;
        }
        return out;
    }
    
    double stdev() {
//TODO: note that stdev only works with 1D param space. need to check
        int ii=0;
        double sum1=0;
        double sum2=0;
        ListIterator iter = paramDomain.listIterator();
        while (iter.hasNext()) {
            double d = (double)(iter.nextIndex());
            sum1+=posterior[ii]*d*d;
            sum2+=posterior[ii]*d;
            ii++;
        }
        return Math.sqrt(sum1 - sum2*sum2);
    }
    
    double entropy() {
        
        double sum1=0;
        double  lg2 = Math.log(2);
        for (int ii=0;ii<posterior.length; ii++) {
            double log = Math.log(posterior[ii])/lg2;
            if (!Double.isInfinite(log) && !Double.isNaN(log)) {
                sum1 -= log*posterior[ii];
            }
        }
        return sum1;
        //H = -nansum(obj.posterior .* log2(obj.posterior), 2);

    }
    void printList(List l) {
        ListIterator iter = l.listIterator();
        
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
    void printArray(double[] d) {
        for (int ii=0;ii<d.length; ii++) {
            System.out.println(d[ii]);
        }
    }
    void printArray(double[][] d) {
        for (int ii=0;ii<d.length; ii++) {
            for (int jj=0;jj<d[0].length; jj++) {
                System.out.println(d[ii][jj]);
            
            }
        }
    }
    void printArray(double[][][] d) {
        for (int ii=0;ii<d.length; ii++) {
            for (int jj=0;jj<d[0].length; jj++) {
                for (int kk=0;kk<d[0][0].length; kk++) {
                System.out.print(d[ii][jj][kk] +" ");            
            }
            System.out.println(";");
        }
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
    private static <T> List<List<T>> row2Col(List<List<T>> paramD) {
        // turn a list of a list to 
        // a list of 1 item lists
            ListIterator iter = paramD.listIterator();
            ArrayList paramDomain = new ArrayList();
            ArrayList row = (ArrayList)iter.next();
            ListIterator   iter2 = row.listIterator();
            while (iter2.hasNext()) {
                List priorTmp = new ArrayList();
                priorTmp.add(iter2.next());
                paramDomain.add(priorTmp);
            }
            return paramDomain;
    }
    
    }
