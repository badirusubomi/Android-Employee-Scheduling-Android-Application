package com.example.schedulerapp

// Possible to make secondary constructor into primary constructor
class Shift {
    var shiftID: Int = 0
    var dateShift: String = ""
    var timeShift: String = ""

    constructor (shiftID:Int = 0, dateShift: String, timeShift:String){
        this.shiftID = shiftID
        this.dateShift = dateShift
        this.timeShift = timeShift
    }
}