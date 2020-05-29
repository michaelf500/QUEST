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
    public int getNParams() {
    return 4; // no. of values getValue is expecting in vals.
    } 
    public double getValue(double x,double[] vals) {
        double m=0;
        double s=0;
        double l=0;
        double g=0.5;
        if (vals.length >=4) {
            g=vals[3]; 
        }
        if  (vals.length >=3) {
            l=vals[2];
        }
        if  (vals.length >=2) {
            s=vals[1];
        } 
        if  (vals.length >=1) {
            m=vals[0];
        }                
        return (g+(1-l-g)*(new NormalDistribution(m,s)).cumulativeProbability(x));
    }
            
}
