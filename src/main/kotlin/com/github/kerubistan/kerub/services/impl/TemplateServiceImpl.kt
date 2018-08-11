package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.TemplateDao
import com.github.kerubistan.kerub.model.Template
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.TemplateService

class TemplateServiceImpl(
		dao : TemplateDao,
		accessController: AssetAccessController
) : TemplateService, AbstractAssetService<Template>(accessController, dao, "template")