package com.github.K0zka.kerub.data.dynamic

import com.github.K0zka.kerub.data.DaoOperations
import com.github.K0zka.kerub.model.dynamic.ControllerDynamic

public interface ControllerDynamicDao
: DaoOperations.Add<ControllerDynamic, String>,
  DaoOperations.Read<ControllerDynamic, String>,
  DaoOperations.SimpleList<ControllerDynamic> {
}