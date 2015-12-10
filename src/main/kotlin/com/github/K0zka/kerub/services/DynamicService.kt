package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.dynamic.DynamicEntity

interface DynamicService<T : DynamicEntity> : RestOperations.Read<T>