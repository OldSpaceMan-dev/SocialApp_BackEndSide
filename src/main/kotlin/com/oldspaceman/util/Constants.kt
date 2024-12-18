package com.oldspaceman.util

object Constants {


    const val UNEXPECTED_ERROR_MESSAGE = "An unexpected error has occurred, try again!"
    const val MISSING_PARAMETERS_ERROR_MESSAGE = "Missing required parameters!"

    const val BASE_URL = "http://192.168.1.106:8080/"//"http://0.0.0.0:8080/"

    const val POST_IMAGES_FOLDER = "post_images/"
    const val POST_IMAGES_FOLDER_PATH = "build/resources/main/static/$POST_IMAGES_FOLDER"

    const val PROFILE_IMAGES_FOLDER = "profile_images/"
    const val PROFILE_IMAGES_FOLDER_PATH = "build/resources/main/static/$PROFILE_IMAGES_FOLDER"

    const val DEFAULT_PAGE_SIZE = 10
    const val SUGGESTED_FOLLOWING_LIMIT = 15

    const val PAGE_NUMBER_PARAMETER = "page"
    const val PAGE_LIMIT_PARAMETER = "limit"
    const val USER_ID_PARAMETER = "userId"
}