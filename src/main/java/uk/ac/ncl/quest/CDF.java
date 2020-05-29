/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.quest;

/**
 *
 * @author Michael
 */
public interface CDF {
    double getValue(double x,double mean , double sd, double lambda, double gamma);
}
