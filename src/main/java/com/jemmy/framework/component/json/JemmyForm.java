package com.jemmy.framework.component.json;

public class JemmyForm extends JemmyJson {

    public String toFormDataString() {
        StringBuilder stringBuilder = new StringBuilder();

        final int[] i = {0};

        this.forEach((k, v) -> {
            if (i[0] != 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(k);
            stringBuilder.append("=");
            stringBuilder.append(v);

            i[0]++;
        });

        return stringBuilder.toString();
    }

}
