package com.ke.jdaddresspicker.demo

data class CityBean(
    val children: List<CityBean>?,
    val label: String,
    val value: String,
    val parentId: String
)
