package com.example.mds

class User {
    var username: String? = null
    var email: String? = null
    var role: String? = null
    var deviceToken: String? = null


    constructor(){}
    constructor(username: String?, email: String?, role: String?, deviceToken: String?) {
        this.username = username
        this.email = email
        this.role = role
        this.deviceToken = deviceToken
    }


}