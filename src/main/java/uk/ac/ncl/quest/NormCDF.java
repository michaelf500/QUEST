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
 * getVal(x,m,s,l,g) returns g + (1-l-g)normalPSD(x:m,s)
 * @author Michael
 */
public class NormCDF implements CDF{
//    NormalDistribution nd ;
//    NormCDF(double m, double s) {
//        nd = ;
//    }
    public double getValue(double x,double m,double s, double l,double g) {
        return (g+(1-l-g)*(new NormalDistribution(m,s)).cumulativeProbability(x));
    }
            
}
