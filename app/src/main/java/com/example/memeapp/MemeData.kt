package com.example.memeapp

class MemeData(private val mImageUrl:String,
               private val mAuthor:String,
               private val mRedditSource:String,
               private val mPostLink:String) {

    fun getmImageUrl():String{
        return mImageUrl
    }
    fun getmAuthor():String{
        return mAuthor
    }
    fun getmRedditSource():String{
        return mRedditSource
    }
    fun getmPostLink():String{
        return mPostLink
    }
}