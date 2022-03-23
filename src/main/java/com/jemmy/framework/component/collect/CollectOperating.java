package com.jemmy.framework.component.collect;

import com.jemmy.framework.auto.page.operating.Operating;
import com.jemmy.framework.auto.page.operating.OperatingType;
import com.jemmy.framework.auto.page.operating.TableOperating;

public class CollectOperating implements TableOperating {

    Operating entity = new Operating(OperatingType.ENTITY, "查看");

}
