package com.example.htan.myapplication;

/**
 * Created by htan on 09/02/2015.
 */


class MyCalendar {

    public String name;

    public String id;

    public MyCalendar(String _name, String _id) {

        name = _name;

        id = _id;

    }

    @Override

    public String toString() {

        return name;

    }

}

