package com.ogorodnik.hibernate;

@Table (name = "persons")
public class PersonWithoutId {
    @Column
    private int x;

    @Column(name = "person_name")
    private String name;

    @Column
    private double salary;

    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getSalary(){
        return salary;
    }

    public void setSalary(double salary){
        this.salary = salary;
    }
}
