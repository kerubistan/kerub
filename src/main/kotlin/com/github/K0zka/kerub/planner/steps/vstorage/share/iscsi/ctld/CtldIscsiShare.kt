package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiShare

class CtldIscsiShare(override val host: Host, override val vstorage: VirtualStorageDevice) : AbstractIscsiShare