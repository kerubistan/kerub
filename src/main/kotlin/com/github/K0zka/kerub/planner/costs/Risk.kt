package com.github.K0zka.kerub.planner.costs

/**
 * In certain cases the planner can make a step which leads to
 * soft expectations (Want and Hint) not being met. In such cases
 * a Risk should be added to the list of costs.
 */
data class Risk(
		/**
		 * The score of the risk. The bigger the score, the bigger the issue and should make more effort to avoid
		 */
		val score: Int,
		/**
		 * A comment about the nature of the risk
		 */
		val comment: String) : Cost