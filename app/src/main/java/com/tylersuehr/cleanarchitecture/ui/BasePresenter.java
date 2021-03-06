package com.tylersuehr.cleanarchitecture.ui;
import com.tylersuehr.cleanarchitecture.domain.UseCaseScheduler;
/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 7/3/2017.
 *
 * This is the base presenter for all presenters.
 */
public abstract class BasePresenter<T> {
    protected final UseCaseScheduler scheduler = UseCaseScheduler.getInstance();
    private T view;


    public BasePresenter() {}

    public BasePresenter(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }

    public void setView(T view) {
        this.view = view;
    }
}