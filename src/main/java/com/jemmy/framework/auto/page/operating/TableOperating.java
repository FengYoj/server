package com.jemmy.framework.auto.page.operating;

public interface TableOperating {
    default Operating delete() {
        return new Operating(OperatingType.DELETE);
    }

    default Operating edit() {
        return new Operating(OperatingType.EDIT);
    }
}
