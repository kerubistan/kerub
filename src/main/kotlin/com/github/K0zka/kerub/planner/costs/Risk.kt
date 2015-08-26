package com.github.K0zka.kerub.planner.costs

/**
 * In certain cases the planner can make a step which leads to
 * soft expectations (Want and Hint) not being met. In such cases
 * a Risk should be added to the list of costs.
 */
data class Risk(score : Int, comment : String) : Cost