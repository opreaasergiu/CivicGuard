package com.example.mds

import java.io.Serializable

class ComplaintModel : Serializable {
    var title: String? = null
    var description: String? = null
    var status: Boolean? = true
    var institutionName: String? = null
    var userId:String? = null
    var locationAddress:String? = null
    var locationLatitude:String? = null
    var locationLongitude:String? = null
    var imageUrl:String? = null
    var creationDate: String? = null

   constructor()
   {

   }
    constructor(title: String?,description: String?, status: Boolean?, institutionName: String?, userId:String?,locationAddress:String?,  locationLatitude:String?, locationLongitude:String?, imageUrl:String?, creationDate: String?)
    {
        this.title = title
        this.description = description
        this.status = status
        this.institutionName = institutionName
        this.userId = userId
        this.locationAddress = locationAddress
        this.locationLatitude = locationLatitude
        this.locationLongitude = locationLongitude
        this.imageUrl = imageUrl
        this.creationDate = creationDate

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComplaintModel

        if (title != other.title) return false
        if (description != other.description) return false
        if (status != other.status) return false
        if (institutionName != other.institutionName) return false
        if (userId != other.userId) return false
        if (locationAddress != other.locationAddress) return false
        if (locationLatitude != other.locationLatitude) return false
        if (locationLongitude != other.locationLongitude) return false
        if (imageUrl != other.imageUrl) return false
        if (creationDate != other.creationDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (institutionName?.hashCode() ?: 0)
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + (locationAddress?.hashCode() ?: 0)
        result = 31 * result + (locationLatitude?.hashCode() ?: 0)
        result = 31 * result + (locationLongitude?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (creationDate?.hashCode() ?: 0)
        return result
    }
}
