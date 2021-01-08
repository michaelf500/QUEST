/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.quest;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * A cumulative normal distribution, with 4 parameters
 * mean, sd, lambda (lapse rate), gamma (probability of guessing)
 * getVal(x,[m,s,l,g]) returns g + (1-l-g)normalPSD(x:m,s)
 * @author Michael
 */
public class NormCDF implements CDF{
//    NormalDistribution nd ;
//    NormCDF(double m, double s) {
//        nd = ;
//    }
//        double m;//=0
//        double s;//=1
//        double l;//=0
//        double g;//=0.5
        double[] params = {0,1,0,0.5};
        int nparams = 0;
        
        NormCDF(double [] par) {
            /* par is an array of size 4, 
            with those values to be estimated as Double.NaN
            fixed values are passed. 
            in order mean, sd,lambda, gamma
            */
            if (par.length!=params.length) {
                throw new IllegalArgumentException("Wrong size array "+
                        par.length+"- should be "+params.length);
            }
            for (int j=0;j<par.length; j++) {
                params[j] = par[j];
                if (par[j]==Double.NaN) {
                    nparams++;
                }
            }
        }
    public int getNParams() {
        
        return nparams; // no. of values getValue is expecting in vals.
    } 
    public double getValue(double x,double[] vals) {
        if (vals.length!=nparams) {
                throw new IllegalArgumentException("Wrong size array "+
                        vals.length+"- should be "+nparams);
            }
        int j=0;
        double[] par = new double [params.length];
        for (int i=0; i<params.length; i++) {
            if (params[i] == Double.NaN) {
                par[i] = vals[j];
                j++;
            } else {
                par[i] = params[i];
            }
                
        }
        //throw new IllegalArgumentException("Wrong type passed");

//            g=vals[3]; 
//            l=vals[2];
//            s=vals[1];
//            m=vals[0];
        return (par[3]+(1-par[2]-par[3])*
                (new NormalDistribution(par[0],par[1])).cumulativeProbability(x));
    }
            
}
