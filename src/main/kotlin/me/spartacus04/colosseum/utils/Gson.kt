package me.spartacus04.colosseum.utils

import com.google.gson.GsonBuilder
import com.google.gson.Strictness

object Gson {
    val GSON = GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create()
}