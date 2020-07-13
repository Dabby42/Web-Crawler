package com.gsd.kolorbi.service;

public interface CounterGenerator {

    long getNextCounter(String type);

    long getCount(String type);
}
