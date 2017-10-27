package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Account
import java.util.UUID

interface AccountDao : ListableCrudDao<Account, UUID>, DaoOperations.SimpleSearch<Account>