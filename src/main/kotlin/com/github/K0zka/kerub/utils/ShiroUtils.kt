package com.github.K0zka.kerub.utils

import org.apache.shiro.SecurityUtils
import org.apache.shiro.util.ThreadContext

/**
 * Get current shiro user - throws exception if no user
 */
fun currentUser() = SecurityUtils.getSubject()?.principal?.toString()
