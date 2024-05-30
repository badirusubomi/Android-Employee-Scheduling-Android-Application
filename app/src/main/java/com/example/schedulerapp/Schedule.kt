package com.example.schedulerapp

// Possible to make secondary constructor to primary constructor
class Schedule {

    var shiftID: Int = 0
    var dateShift: String = ""
    var employeeID: Int = 0

    constructor (shiftID:Int, dateShift: String, employeeID:Int){
        this.shiftID = shiftID
        this.dateShift = dateShift
        this.employeeID = employeeID
    }
}