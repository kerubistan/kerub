package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Account
import java.util.UUID

interface AccountDao : ListableCrudDao<Account, UUID>, DaoOperations.SimpleSearch<Account>