package com.github.K0zka.kerub.services


public class ResultPage<T>(val start: Long = 0,
                           val count: Long,
                           val total: Long,
                           val sortBy: String = "id",
                           val result: List<T>) {
}