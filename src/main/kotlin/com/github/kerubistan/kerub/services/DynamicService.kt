package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.dynamic.DynamicEntity

interface DynamicService<T : DynamicEntity> : RestOperations.Read<T>