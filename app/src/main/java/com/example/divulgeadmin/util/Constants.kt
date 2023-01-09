package com.example.divulgeadmin.util

import com.example.divulgeadmin.R
import com.example.divulgeadmin.models.Country
import com.example.divulgeadmin.models.ExclusiveNews

class Constants {
    companion object {
        const val IS_DEBUG = true

        const val DEFAULT_EXCLUSIVE_NEWS_SOURCE = "Divulge News"

        // Firebase user values.
        const val EXCLUSIVE_NEWS_IMAGES = "exclusiveNewsImages"
        const val PROFILE_IMAGES = "profileImages"
        const val USERS = "users"
        const val FULL_NAME = "fullName"
        const val EMAIL = "email"
        const val COUNTRY_CODE = "countryCode"
        const val USER_ROLE = "userRole"
        const val UID = "uid"
        const val IS_USER_DEFAULT_VALUE = true
        const val USER_DEFAULT_COUNTRY_VALUE = "us"

        // Firebase exclusive news values.
        const val EX_NEWS_ID = "id"
        const val EX_NEWS_AUTHOR = "author"
        const val EX_NEWS_TITLE = "title"
        const val EX_NEWS_CONTENT = "content"
        const val EX_NEWS_DESCRIPTION = "description"
        const val EX_NEWS_PUBLISHED_AT = "publishedAt"
        const val EX_NEWS_CREATED_BY = "createdBy"
        const val EX_NEWS_ACTIVE_STATUS = "activeStatus"
        const val EX_NEWS_COUNTRY = "country"
        const val EX_NEWS_SOURCE = "source"
        const val EX_NEWS_URL = "url"
        const val EX_NEWS_URL_TO_IMAGE = "urlToImage"

        // Form Submit modes
        const val FORM_SUBMIT_MODE = "formSubmitMode"
        const val FORM_SUBMIT_MODE_ADD = "add"
        const val FORM_SUBMIT_MODE_UPDATE = "update"

        // Models
        const val MODEL_EXCLUSIVE_NEWS = "exclusiveNews"

        val EXCLUSIVE_NEWS: List<ExclusiveNews> = listOf(
            ExclusiveNews(
                "1", "Reuters", "Tesla offers discount on some car models in U.S., Canada",
                "Tesla Inc is offering discounts on Model 3 and Model Y vehicles delivered in the United States and Canada this month, sales pages on its website showed on Wednesday, amid concerns the automaker is fa… [+1432 chars]",
                "Tesla Inc is offering discounts on Model 3 and Model Y vehicles delivered in the United States and Canada this month, sales pages on its website showed on Wednesday, amid concerns the automaker is facing softening demand as economies slow",
                "2022-12-22T05:33:18Z",
                "admin",
                true,
                "us",
                "Divulge News",
                "https://www.livemint.com/companies/news/tesla-offers-discount-on-some-car-models-in-u-s-canada-11671686769952.html",
                "https://images.livemint.com/img/2022/12/22/600x338/TESLA-STOCKS--0_1671629330156_1671629330156_1671686810866_1671686810866.JPG",
            ),
            ExclusiveNews(
                "2", "Reuters", "Tesla offers discount on some car models in U.S., Canada",
                "Tesla Inc is offering discounts on Model 3 and Model Y vehicles delivered in the United States and Canada this month, sales pages on its website showed on Wednesday, amid concerns the automaker is fa… [+1432 chars]",
                "Tesla Inc is offering discounts on Model 3 and Model Y vehicles delivered in the United States and Canada this month, sales pages on its website showed on Wednesday, amid concerns the automaker is facing softening demand as economies slow",
                "2022-12-22T05:33:18Z",
                "admin",
                true,
                "us",
                "Divulge News",
                "https://www.livemint.com/companies/news/tesla-offers-discount-on-some-car-models-in-u-s-canada-11671686769952.html",
                "https://images.livemint.com/img/2022/12/22/600x338/TESLA-STOCKS--0_1671629330156_1671629330156_1671686810866_1671686810866.JPG",
            ),
            ExclusiveNews(
                "3", "Reuters", "Tesla offers discount on some car models in U.S., Canada",
                "Tesla Inc is offering discounts on Model 3 and Model Y vehicles delivered in the United States and Canada this month, sales pages on its website showed on Wednesday, amid concerns the automaker is fa… [+1432 chars]",
                "Tesla Inc is offering discounts on Model 3 and Model Y vehicles delivered in the United States and Canada this month, sales pages on its website showed on Wednesday, amid concerns the automaker is facing softening demand as economies slow",
                "2022-12-22T05:33:18Z",
                "admin",
                true,
                "us",
                "Divulge News",
                "https://www.livemint.com/companies/news/tesla-offers-discount-on-some-car-models-in-u-s-canada-11671686769952.html",
                "https://images.livemint.com/img/2022/12/22/600x338/TESLA-STOCKS--0_1671629330156_1671629330156_1671686810866_1671686810866.JPG",
            )
        )

        val COUNTRIES = arrayListOf<Country>(
            Country(1, "United Arab Emirates", "ae", R.drawable.uae),
            Country(2, "Argentina", "ar", R.drawable.argentina),
            Country(3, "Austria", "at", R.drawable.austria),
            Country(4, "United States of America", "us", R.drawable.usa),
        )
    }
}