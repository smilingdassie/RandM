package com.dsouchon.wayo.visualfusion;

/**
 * Created by daniel on 11/04/2017.
 */

public class Tree{

    public int age;
    public double height;

    public void growOneYear()
    {
        age++;
    }

    public void show()
    {
        System.out.println("Træet er " +age+ " år gammelt og " +height+ " meter højt.");
    }
}