package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.ExecutionResult
import java.util.UUID

interface ExecutionResultDao :
		DaoOperations.Add<ExecutionResult, UUID>,
		DaoOperations.PagedList<ExecutionResult, UUID>,
		DaoOperations.SimpleSearch<ExecutionResult>
