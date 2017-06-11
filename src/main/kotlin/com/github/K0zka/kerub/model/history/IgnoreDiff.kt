package com.github.K0zka.kerub.model.history

import java.lang.annotation.ElementType
import java.lang.annotation.Target

@Retention(AnnotationRetention.RUNTIME)
@Target(ElementType.METHOD)
annotation class IgnoreDiff