package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.ExecutionResult
import java.util.UUID

interface ExecutionResultDao :
		DaoOperations.Add<ExecutionResult, UUID>,
		DaoOperations.PagedList<ExecutionResult, UUID>,
		DaoOperations.SimpleSearch<ExecutionResult>
