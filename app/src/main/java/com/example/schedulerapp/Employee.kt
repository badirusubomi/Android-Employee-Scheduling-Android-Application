package com.example.schedulerapp
import java.io.Serializable

// Possible to make secondary constructor into primary constructor

/** This class creates an instance of am employee to be inputted into the database. */
class Employee : Serializable {
    var empId: Int = 1
    var fName: String = ""
    var lName: String = ""
    var phone: String = ""
    var email: String = ""
    var amQ: Boolean = false
    var pmQ: Boolean = false

    constructor (empId:Int = 1, fName:String, lName: String, phone:String, email:String, aMq: Boolean, pMq:Boolean){
        this.fName = fName
        this.lName = lName
        this.phone = phone
        this.email = email
        this.amQ = aMq
        this.pmQ= pMq
        this.empId = empId
    }
}
